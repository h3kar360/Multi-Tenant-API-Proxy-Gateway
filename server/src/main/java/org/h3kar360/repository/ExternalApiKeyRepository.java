package org.h3kar360.repository;

import org.h3kar360.model.ExternalAPIKey;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ExternalApiKeyRepository extends ReactiveCrudRepository<ExternalAPIKey, UUID> {
}
