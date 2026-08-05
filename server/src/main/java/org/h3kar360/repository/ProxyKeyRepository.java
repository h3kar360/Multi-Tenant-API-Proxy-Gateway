package org.h3kar360.repository;

import org.h3kar360.model.ProxyCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProxyKeyRepository extends JpaRepository<ProxyCredential, Long> {
    Optional<ProxyCredential> findByClientId(long clientId);
}
