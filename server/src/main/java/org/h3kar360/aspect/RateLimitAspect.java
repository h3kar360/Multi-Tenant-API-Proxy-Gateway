package org.h3kar360.aspect;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.h3kar360.exception.RateLimitException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final Supplier<BucketConfiguration> bucketConfiguration;
    private final ProxyManager<String> proxyManager;

    @Before("@annotation(org.h3kar360.annotation.RateLimitProtected)")
    public void rateLimit() {
        String key = getCurrentClientEmail();
        Bucket bucket = proxyManager.builder().build(key, bucketConfiguration);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        System.out.println(probe.getRemainingTokens());

        if(!probe.isConsumed()) {
            throw new RateLimitException("Rate limit exceeded");
        }
    }

    private String getCurrentClientEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        return auth.getName();
    }
}
