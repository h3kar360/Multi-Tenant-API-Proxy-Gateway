package org.h3kar360.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import lombok.RequiredArgsConstructor;
import org.h3kar360.model.Client;
import org.h3kar360.repository.ClientRepository;
import org.h3kar360.repository.ProxyKeyRepository;
import org.h3kar360.util.HashUtil;
import org.h3kar360.util.ProxyKeyHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicBucketConfiguration implements Supplier<BucketConfiguration> {
    private final ProxyKeyHolder proxyKeyHolder;
    private final ProxyKeyRepository proxyKeyRepository;

    @Override
    public BucketConfiguration get() {
        String proxyKey = proxyKeyHolder.get();
        System.out.println(proxyKey);

        Client client = proxyKeyRepository.findByProxyKey(HashUtil.hashKey(proxyKey))
                .orElseThrow(() -> new RuntimeException("Invalid proxy key")).getClient();


        int rateLimit = client.getRateLimit();
        int rateLimitWindow = client.getRateLimitWindow();

        return BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(rateLimit, Duration.ofMinutes(rateLimitWindow)))
                .build();
    }
}
