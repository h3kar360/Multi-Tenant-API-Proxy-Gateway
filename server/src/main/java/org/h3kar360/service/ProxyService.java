package org.h3kar360.service;

import jakarta.servlet.http.HttpServletRequest;
import org.h3kar360.dto.ProxyRequestDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class ProxyService {
    // cache for rest clients based on timeouts
    ConcurrentHashMap<String, RestClient> restClientCache = new ConcurrentHashMap<>();

    public ResponseEntity<byte[]> forwardRequest(ProxyRequestDto proxyRequest) {
        // extracting all the fields of the dto
        final HttpServletRequest request = proxyRequest.getRequest();
        final HttpMethod method = proxyRequest.getMethod();
        final String apiName = proxyRequest.getApiName();
        final byte[] body = proxyRequest.getBody();

        // hard coded first for testing
        String baseUrl = "https://" + apiName;
        int connectionTimeout = 30000;
        int readTimeout = 30000;

        String cacheKey = "%d:%d".formatted(connectionTimeout, readTimeout);

        // create the rest client with custom timeouts
        RestClient restClient = restClientCache.computeIfAbsent(cacheKey, k -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(connectionTimeout);
            factory.setReadTimeout(readTimeout);

            return RestClient.builder().requestFactory(factory).build();
        });

        Consumer<HttpHeaders> headersConsumer = headers -> {
            request.getHeaderNames().asIterator().forEachRemaining(name -> {
                if(!name.equalsIgnoreCase("host")) {
                    headers.add(name, request.getHeader(name));
                }
            });
        };

        String path = request.getRequestURI();
        String query = request.getQueryString();
        String apiUri = path.replace("/proxy/v1/gateway", "");

        String targetUrl = query != null && !query.isEmpty()
                ? "https:/" + apiUri + "?" + query
                : "https:/" + apiUri;

        RestClient.RequestBodySpec reqSpec = restClient
                .method(method)
                .uri(targetUrl)
                .headers(headersConsumer);

        if(body != null)
            reqSpec.body(body);

        return reqSpec
                .retrieve()
                .toEntity(byte[].class);
    }
}
