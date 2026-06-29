package org.h3kar360.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "client_email")
    private String clientEmail;

    @Column(nullable = false, name = "client_name", unique = true)
    private String clientName;

    @Column(nullable = false, name = "client_password")
    private String clientPassword;

    @Column(nullable = false, name = "rate_limit")
    private Integer rateLimit;

    @Column(nullable = false, name = "rate_limit_window")
    private Integer rateLimitWindow;

    @Column(nullable = false, name = "is_active")
    private boolean isActive;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Api> apis;
}
