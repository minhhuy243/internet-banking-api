package com.internetbanking.service;

import com.internetbanking.entity.User;
import com.internetbanking.entity.UserRefreshToken;
import com.internetbanking.repository.UserRefreshTokenRepository;
import com.internetbanking.repository.UserRepository;
import com.internetbanking.request.LoginRequest;
import com.internetbanking.request.UserCreateRequest;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public User create(UserCreateRequest request) {
//        User user = User.builder()
//                .email(request.getUsername())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .build();

//        return userRepository.save(user);
        return null;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtProvider.generateJwtToken(authentication);
        String refreshToken = jwtProvider.generateJwtRefreshToken(authentication);
//        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException());
        User user = null;
        if(user.getRefreshToken() != null) {
            user.getRefreshToken().setToken(refreshToken);
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

    public RefreshTokenResponse getRefreshToken(String refreshToken) {
        UserRefreshToken refreshTokenEntity = userRefreshTokenRepository.findByToken(refreshToken);
        String username = jwtProvider.getUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtProvider.generateJwtToken(authentication);
        refreshTokenEntity.setToken(accessToken);
        userRefreshTokenRepository.save(refreshTokenEntity);

        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
        refreshTokenResponse.setAccessToken(jwtProvider.generateJwtToken(authentication));
        return refreshTokenResponse;
    }
}
