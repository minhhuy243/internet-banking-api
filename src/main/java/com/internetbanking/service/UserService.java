package com.internetbanking.service;

import com.internetbanking.entity.Account;
import com.internetbanking.entity.User;
import com.internetbanking.entity.UserRefreshToken;
import com.internetbanking.entity.type.AccountType;
import com.internetbanking.repository.AccountRepository;
import com.internetbanking.repository.RoleRepository;
import com.internetbanking.repository.UserRefreshTokenRepository;
import com.internetbanking.repository.UserRepository;
import com.internetbanking.request.LoginRequest;
import com.internetbanking.request.RefreshTokenRequest;
import com.internetbanking.request.UserRequest;
import com.internetbanking.response.LoginResponse;
import com.internetbanking.response.RefreshTokenResponse;
import com.internetbanking.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

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

    @Transactional
    public User register(UserRequest request) {
        User user = new User().toBuilder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .birthday(request.getBirthday())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();
        if (securityService.getRole().equals("ROLE_EMPLOYEE")) {
            user.setRole(roleRepository.findByCode("ROLE_CUSTOMER"));
        } else {
            user.setRole(roleRepository.findByCode("ROLE_EMPLOYEE"));
        }
//        user.setRole(roleRepository.findByCode("ROLE_CUSTOMER"));
        User newUser = userRepository.saveAndFlush(user);

        Random rand = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int n = rand.nextInt(10);
            accountNumber.append(n);
        }
        Account account = new Account().toBuilder()
                .accountNumber(Long.valueOf(accountNumber.toString()))
                .dateOpened(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .type(AccountType.PAYMENT)
                .user(newUser)
                .build();
        accountRepository.save(account);
        newUser.setPassword(null);
        return newUser;
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
}
