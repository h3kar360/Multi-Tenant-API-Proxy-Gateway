package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ProxyRequestDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyServiceCached {
    private final ProxyService proxyService;

    @Cacheable(value = "response", key = "#url", unless = "#result == null")
    public ResponseEntity<byte[]> forwardRequestCached(String url, ProxyRequestDto proxyRequest) {
        return proxyService.forwardRequest(proxyRequest);
    }
}
