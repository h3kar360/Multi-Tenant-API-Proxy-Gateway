package org.h3kar360.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("external_api_key")
public class ExternalAPIKey implements Persistable<UUID>{
    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("api_name")
    private String apiName;

    @Column("target_base_url")
    private String targetBaseUrl;

    @Column("encrypted_api_key")
    private String encryptedApiKey;

    @Column("proxy_key_id")
    private UUID proxyKeyId;

    @Transient
    private boolean isNewRow = false; // Default to false for DB fetches

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        // If id is null OR we explicitly marked it as a new row, it's an INSERT
        return this.isNewRow || this.id == null;
    }

    // Helper method for the service layer to explicitly declare an intent to insert
    public ExternalAPIKey markNew() {
        this.isNewRow = true;
        return this;
    }
}
