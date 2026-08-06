package org.h3kar360.aspect;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.h3kar360.exception.RateLimitException;
import org.h3kar360.model.Client;
import org.h3kar360.model.ProxyCredential;
import org.h3kar360.service.ProxyKeyService;
import org.h3kar360.util.ProxyKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.function.Supplier;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final Supplier<BucketConfiguration> bucketConfiguration;
    private final ProxyManager<String> proxyManager;
    private final ProxyKeyHolder proxyKeyHolder;

    @Before("@annotation(org.h3kar360.annotation.RateLimitProtected)")
    public void rateLimit() {
        // Get current request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String proxyKey = request.getHeader("X-Proxy-Key");
        if(proxyKey == null || proxyKey.isEmpty())
            throw new RuntimeException("Missing proxy key");

        proxyKeyHolder.set(proxyKey);

        try {
            Bucket bucket = proxyManager.builder().build(proxyKey, bucketConfiguration);

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            System.out.println(probe.getRemainingTokens());

            if(!probe.isConsumed()) {
                throw new RateLimitException("Rate limit exceeded");
            }
        } finally {
            proxyKeyHolder.clear();
        }
    }
}
