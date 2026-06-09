package org.h3kar360.repository;

import org.h3kar360.model.ProxyKey;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProxyKeyRepository extends ReactiveCrudRepository<ProxyKey, UUID> {
    Mono<ProxyKey> findByProxySecretToken(String token);
}
