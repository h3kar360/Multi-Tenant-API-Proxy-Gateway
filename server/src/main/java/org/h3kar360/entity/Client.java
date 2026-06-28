package org.h3kar360.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, name = "client_name")
    private String clientName;

    // unique because will be hashed
    @Column(nullable = false, name = "client_password", unique = true)
    private String clientPassword;

    @Column(nullable = false, name = "rate_limit")
    private Integer rateLimit;

    @Column(nullable = false, name = "rate_limit_window")
    private Integer rateLimitWindow;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Api> apis;
}
