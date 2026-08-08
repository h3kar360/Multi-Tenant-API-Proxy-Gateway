package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.model.Client;
import org.h3kar360.model.RefreshToken;
import org.h3kar360.repository.RefreshTokenRepository;
import org.h3kar360.util.HashUtil;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int keyBytes = 32;

    public String generateRefreshToken(Client client) {
        byte[] bytes = new byte[keyBytes];
        secureRandom.nextBytes(bytes);
        String rawRefresh = "refresh_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(HashUtil.hashKey(rawRefresh));
        refreshToken.setClient(client);

        refreshTokenRepository.save(refreshToken);

        return rawRefresh;
    }

    public Client validateAndGetClient(String refreshToken) {
        RefreshToken refreshTokenData = refreshTokenRepository.findByRefreshToken(HashUtil.hashKey(refreshToken))
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if(LocalDateTime.now().isAfter(refreshTokenData.getExpiredAt())) {
            return null;
        }

        return refreshTokenData.getClient();
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
