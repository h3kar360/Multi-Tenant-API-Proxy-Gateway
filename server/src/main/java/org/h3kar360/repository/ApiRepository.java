package org.h3kar360.repository;

import org.h3kar360.model.Api;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiRepository extends JpaRepository<Api, Long> {
    List<Api> findByClientId(long clientId);
    Optional<Api> findByIdAndClientId(long apiId, long clientId);
}
