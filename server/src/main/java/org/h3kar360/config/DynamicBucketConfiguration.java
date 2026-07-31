package org.h3kar360.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import lombok.RequiredArgsConstructor;
import org.h3kar360.model.Client;
import org.h3kar360.repository.ClientRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicBucketConfiguration implements Supplier<BucketConfiguration> {
    private final ClientRepository clientRepository;

    @Override
    public BucketConfiguration get() {
        String clientEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Client client = clientRepository.findByClientEmail(clientEmail)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        int rateLimit = client.getRateLimit();
        int rateLimitWindow = client.getRateLimitWindow();

        return BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(rateLimit, Duration.ofMinutes(rateLimitWindow)))
                .build();
    }
}
