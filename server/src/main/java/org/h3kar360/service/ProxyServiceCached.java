package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.CachedResponseDto;
import org.h3kar360.dto.ProxyRequestDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ProxyServiceCached {
    @Lazy
    private final ProxyService proxyService;

    @Cacheable(value = "response", key = "#url", unless = "#result == null")
    public byte[] forwardRequestCached(String url, ProxyRequestDto proxyRequest) {
        long start = System.currentTimeMillis();
        ResponseEntity<byte[]> response = proxyService.forwardRequest(proxyRequest);
        long time = System.currentTimeMillis() - start;
        System.out.println(time);

        return response.getBody();
    }
}
