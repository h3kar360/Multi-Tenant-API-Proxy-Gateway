package org.h3kar360.repository;

import org.h3kar360.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientEmail(String email);
    Optional<Client> findByVerificationCode(String verificationCode);
}
