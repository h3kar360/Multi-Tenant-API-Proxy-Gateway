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

        String path = request.getRequestURI();
        String query = request.getQueryString();

        String url = query == null || query.isEmpty() ? path : path + "?" + query;

        long currTime = System.currentTimeMillis();

        CounterEntry counter = calls.merge(url, new CounterEntry(1, currTime), (oldEntry, newEntry) -> {
            oldEntry.count += 1;
            oldEntry.timeStamp = currTime;
            return oldEntry;
        });

        if(counter.count >= threshold && method == HttpMethod.GET) {
            byte[] cachedResponse = proxyServiceCached.forwardRequestCached(url, proxyRequest);

             return ResponseEntity
                     .ok()
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(cachedResponse);
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
