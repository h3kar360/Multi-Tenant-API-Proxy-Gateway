package org.h3kar360.service;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.CachedResponseDto;
import org.h3kar360.dto.ProxyRequestDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProxyServiceCached {
    @Lazy
    private final ProxyService proxyService;
    private final PathPatternRequestMatcher.Builder builder;

    @Cacheable(value = "response", key = "#cacheKey", unless = "#result == null")
    public CachedResponseDto forwardRequestCached(String cacheKey, ProxyRequestDto proxyRequest) {
        ResponseEntity<byte[]> response = proxyService.forwardRequest(proxyRequest);

        if (response.getBody() == null) {
            return null;
        }

        HttpHeaders upstreamHeaders = response.getHeaders();
        Map<String, List<String>> preservedHeaders = new HashMap<>();

        // Selectively preserve essential payload headers from the target API
        if (upstreamHeaders.get(HttpHeaders.CONTENT_TYPE) != null) {
            preservedHeaders.put(HttpHeaders.CONTENT_TYPE, upstreamHeaders.get(HttpHeaders.CONTENT_TYPE));
        }
        if (upstreamHeaders.get(HttpHeaders.CONTENT_ENCODING) != null) {
            preservedHeaders.put(HttpHeaders.CONTENT_ENCODING, upstreamHeaders.get(HttpHeaders.CONTENT_ENCODING));
        }

        return new CachedResponseDto(preservedHeaders, response.getBody());
    }
}
