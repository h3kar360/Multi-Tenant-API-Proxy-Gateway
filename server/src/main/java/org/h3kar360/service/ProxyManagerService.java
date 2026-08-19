package org.h3kar360.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.CachedResponseDto;
import org.h3kar360.dto.ProxyRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ProxyManagerService {
    private final ProxyServiceCached proxyServiceCached;
    private final ProxyService proxyService;

    @Value("${app.cache.threshold}")
    private int threshold;

    @Value("${app.hash.cleanup.duration}")
    private int expirationTime;

    // cache for api calls
    ConcurrentHashMap<String, CounterEntry> calls = new ConcurrentHashMap<>();

    public ResponseEntity<byte[]> proxyManager(ProxyRequestDto proxyRequest) {
        final HttpServletRequest request = proxyRequest.getRequest();
        final HttpMethod method = proxyRequest.getMethod();
        final String proxyKey = proxyRequest.getProxyKey();

        String path = request.getRequestURI();
        String query = request.getQueryString();

        String url = query == null || query.isEmpty() ? path : path + "?" + query;
        String cacheKey = url + proxyKey;
        System.out.println("Cache key: " + cacheKey);

        long currTime = System.currentTimeMillis();

        CounterEntry counter = calls.merge(cacheKey, new CounterEntry(1, currTime), (oldEntry, newEntry) -> {
            oldEntry.count += 1;
            oldEntry.timeStamp = currTime;
            return oldEntry;
        });

        if(counter.count >= threshold && method == HttpMethod.GET) {
            CachedResponseDto cachedDto = proxyServiceCached.forwardRequestCached(cacheKey, proxyRequest);

            if (cachedDto != null && cachedDto.getBody() != null) {
                HttpHeaders headers = new HttpHeaders();

                // Re-apply preserved response headers dynamically
                if (cachedDto.getHeaders() != null) {
                    for (Map.Entry<String, List<String>> entry : cachedDto.getHeaders().entrySet()) {
                        headers.put(entry.getKey(), entry.getValue());
                    }
                }

                return ResponseEntity
                        .ok()
                        .headers(headers)
                        .body(cachedDto.getBody());
            }
        }

        return proxyService.forwardRequest(proxyRequest);
    }

    @Scheduled(fixedDelay = 600000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        calls.entrySet().removeIf(entry ->
            now - entry.getValue().timeStamp > expirationTime
        );
    }
}

class CounterEntry {
    public int count;
    public long timeStamp;

    public CounterEntry(int count, long timeStamp) {
        this.count = count;
        this.timeStamp = timeStamp;
    }
}
