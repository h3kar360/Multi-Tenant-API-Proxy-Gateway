package org.h3kar360.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Table("proxy_key")
public class ProxyKey {
    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("proxy_secret_token")
    private String proxySecretToken;

    @Column("available_tokens")
    private Integer availableTokens;

    @Column("last_refill_time")
    private Instant lastRefillTime;
}
