package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.model.RefreshToken;
import org.h3kar360.repository.RefreshTokenRepository;
import org.h3kar360.util.HashUtil;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int keyBytes = 32;

    public String generateRefreshToken() {
        byte[] bytes = new byte[keyBytes];
        secureRandom.nextBytes(bytes);
        String rawRefresh = "refresh_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(HashUtil.hashKey(rawRefresh));

        refreshTokenRepository.save(refreshToken);

        return rawRefresh;
    }
}
