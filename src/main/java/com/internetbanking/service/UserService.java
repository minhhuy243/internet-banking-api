package com.internetbanking.service;

import com.internetbanking.dto.AccountDto;
import com.internetbanking.dto.UserDto;
import com.internetbanking.entity.Account;
import com.internetbanking.entity.User;
import com.internetbanking.entity.UserRefreshToken;
import com.internetbanking.entity.type.AccountType;
import com.internetbanking.entity.type.TransactionStatus;
import com.internetbanking.mapper.UserMapper;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.RoleRepository;
import com.internetbanking.repository.UserRefreshTokenRepository;
import com.internetbanking.repository.UserRepository;
import com.internetbanking.request.*;
import com.internetbanking.response.LoginResponse;
import com.internetbanking.response.RefreshTokenResponse;
import com.internetbanking.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final SecurityService securityService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final OtpService otpService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public Page<UserDto> getAll(Pageable pageable) {
        Page<User> accounts = userRepository.findAll(pageable);
        return new PageImpl<>(
                accounts.getContent().stream().map(userMapper::entityToDto).collect(Collectors.toList()),
                accounts.getPageable(),
                accounts.getTotalElements()
        );
    }

    @Transactional
    public UserDto register(UserRequest request) {
        if (securityService.getRole().equals("ROLE_EMPLOYEE") && !request.getRoleCode().equals("ROLE_CUSTOMER")) {
            throw new RuntimeException("Role kh??ng h???p l???!");
        } else if (securityService.getRole().equals("ROLE_ADMIN")
                && (!request.getRoleCode().equals("ROLE_CUSTOMER") &&!request.getRoleCode().equals("ROLE_EMPLOYEE"))) {
            throw new RuntimeException("Role kh??ng h???p l???!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("?????a ch??? Email ???? t???n t???i");
        }

        User user = new User().toBuilder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .birthday(request.getBirthday())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .role(roleRepository.findByCode(request.getRoleCode()).orElseThrow(() -> new RuntimeException("Role kh??ng t???n t???i!")))
                .build();
        User newUser = userRepository.saveAndFlush(user);

        Random rand = new Random();
        StringBuilder accountNumberSb;
        do {
            accountNumberSb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                int n = rand.nextInt(10);
                accountNumberSb.append(n);
            }
        } while (accountRepository.findByAccountNumber(accountNumberSb.toString()).isPresent());

        Account account = new Account().toBuilder()
                .accountNumber(accountNumberSb.toString())
                .dateOpened(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .type(AccountType.PAYMENT)
                .active(true)
                .user(newUser)
                .build();
        newUser.setAccount(account);
        accountRepository.save(account);
//        newUser.setPassword(null);
        return userMapper.entityToDto(newUser);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtProvider.generateJwtToken(request.getEmail());
        String refreshToken = jwtProvider.generateJwtRefreshToken(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException());
        if(user.getRefreshToken() != null) {
            UserRefreshToken refreshTokenEntity = user.getRefreshToken();
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setExpiryDate(jwtProvider.getExpirationDate(refreshToken));
        } else {
            UserRefreshToken refreshTokenEntity = UserRefreshToken.builder()
                    .token(refreshToken)
                    .expiryDate(jwtProvider.getExpirationDate(refreshToken))
                    .build();
            user.addRefreshToken(refreshTokenEntity);
        }
        userRepository.save(user);

//        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
//        Set<String> roles = authorities.stream()
//                .map(authority -> authority.getAuthority())
//                .collect(Collectors.toSet());

        return new LoginResponse().toBuilder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().getCode())
                .build();
    }

    public RefreshTokenResponse getNewToken(RefreshTokenRequest request) {
        UserRefreshToken refreshTokenEntity = userRefreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new RuntimeException());
        User user = refreshTokenEntity.getUser();
        if (jwtProvider.isTokenExpired(refreshTokenEntity.getToken())) {
            refreshTokenEntity.setToken(jwtProvider.generateJwtRefreshToken(user.getEmail()));
            userRefreshTokenRepository.save(refreshTokenEntity);
        }
        return new RefreshTokenResponse().toBuilder()
                .accessToken(jwtProvider.generateJwtToken(user.getEmail()))
                .build();
    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changePassword(UserChangePasswordRequest request) {
        User user = userRepository.findById(securityService.getUserId()).get();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("M???t kh???u c?? kh??ng ????ng!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void sendOTPForgotPassword(String email) throws MessagingException {
        Integer otpValue = otpService.generateOtp(email);
        if (!emailService.sendMessage(email, otpValue, "FORGOT_PASSWORD")) {
            throw new RuntimeException("???? c?? l???i x???y ra!");
        }
    }

    public void validateForgotPassword(Integer otpValue, UserRequest request) {
        Optional<Object> email = Optional.ofNullable(otpService.validateOTP(otpValue));
        if (!email.isPresent()) {
            throw new RuntimeException("OTP kh??ng h???p l???!");
        }
        User user = userRepository.findByEmail(email.get().toString()).get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public UserDto update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User kh??ng t???n t???i"));

        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("?????a ch??? Email ???? t???n t???i");
            }
        }

        if (!user.getPhoneNumber().equals(request.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("S??? ??i???n tho???i ???? t???n t???i");
            }
        }

        if (!request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setBirthday(request.getBirthday());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());

        return userMapper.entityToDto(userRepository.save(user));
    }
}
