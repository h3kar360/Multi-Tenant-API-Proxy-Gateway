package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ProxyKeyResponseDto;
import org.h3kar360.model.Client;
import org.h3kar360.model.ProxyCredential;
import org.h3kar360.repository.ProxyKeyRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ProxyKeyService {
    private final ProxyKeyRepository proxyKeyRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final int keyBytes = 32;

    public String generateRawKey() {
        byte[] bytes = new byte[keyBytes];
        secureRandom.nextBytes(bytes);
        return "pk_live_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashKey(String rawKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawKey.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyKey(String rawKey, String hashKey) {
        return hashKey(rawKey).equals(hashKey);
    }

    public ProxyKeyResponseDto createKey(Client client) {
        ProxyCredential proxy = new ProxyCredential();

        String hashedProxy = hashKey(generateRawKey());

        proxy.setProxyKey(hashedProxy);
        proxy.setClient(client);

        proxyKeyRepository.save(proxy);
        return toDto(hashedProxy);
    }

    public ProxyKeyResponseDto getKey(long clientId) {
        ProxyCredential proxyCredential = proxyKeyRepository.findByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return toDto(proxyCredential.getProxyKey());
    }

    public ProxyKeyResponseDto toDto(String proxyKey) {
        ProxyKeyResponseDto proxyKeyResponseDto = new ProxyKeyResponseDto();
        proxyKeyResponseDto.setProxyKey(proxyKey);
        return proxyKeyResponseDto;
    }
}
