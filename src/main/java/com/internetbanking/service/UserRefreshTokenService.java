package com.internetbanking.service;

import com.internetbanking.entity.UserRefreshToken;
import com.internetbanking.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRefreshTokenService {

    private final UserRefreshTokenRepository userRefreshTokenRepository;

    public void updateRefreshToken(String oldRefreshToken, String newRefreshToken) {
        UserRefreshToken refreshTokenEntity = userRefreshTokenRepository.findByToken(oldRefreshToken);
        refreshTokenEntity.setToken(newRefreshToken);
        userRefreshTokenRepository.save(refreshTokenEntity);
    }
}
