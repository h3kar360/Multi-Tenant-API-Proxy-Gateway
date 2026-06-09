package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.EnvironmentRequest;
import org.h3kar360.model.ExternalAPIKey;
import org.h3kar360.model.ProxyKey;
import org.h3kar360.repository.ExternalApiKeyRepository;
import org.h3kar360.repository.ProxyKeyRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final ExternalApiKeyRepository externalApiKeyRepository;
    private final ProxyKeyRepository proxyKeyRepository;

    public Mono<String> generateProxyKey(UUID userId) {
        // generate unique proxy key token
        String prefix = "psk_dev_";
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);
        String key = HexFormat.of().formatHex(bytes);
        String generatedKey = prefix + key;

        // add all required fields to the database
        ProxyKey proxyKey = new ProxyKey();
        proxyKey.setUserId(userId);
        proxyKey.setProxySecretToken(generatedKey);
        proxyKey.setAvailableTokens(1000);
        proxyKey.setLastRefillTime(OffsetDateTime.now().toInstant());

        return proxyKeyRepository.save(proxyKey)
                .map(savedEntity -> savedEntity.getProxySecretToken());
    }

    public Flux<ExternalAPIKey> insertEnvironments(List<EnvironmentRequest> reqs, UUID userId) {
        return Flux.fromIterable(reqs).flatMap(req -> {
                    return proxyKeyRepository.findByProxySecretToken(req.proxyKeyToken())
                            .map(proxyKey -> {
                                ExternalAPIKey entity = new ExternalAPIKey();

                                entity.setId(UUID.randomUUID());
                                entity.setApiName(req.apiName());
                                entity.setTargetBaseUrl(req.targetBaseUrl());
                                entity.setEncryptedApiKey(req.apiKey());
                                entity.setUserId(userId);
                                entity.setProxyKeyId(proxyKey.getId());

                                return entity.markNew();
                            });
                })
                .flatMap(entity -> externalApiKeyRepository.save(entity));
    }
}
