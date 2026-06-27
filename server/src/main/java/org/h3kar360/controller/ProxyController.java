package org.h3kar360.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.URLConnection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
@RequestMapping("/proxy/v1/gateway")
public class ProxyController {
    // cache for rest clients based on timeouts
    ConcurrentHashMap<String, RestClient> restClientCache = new ConcurrentHashMap<>();

    @RequestMapping("/{api}/**")
    public ResponseEntity<byte[]> proxyGatewayRequest(
            @PathVariable String api,
            HttpServletRequest request,
            @RequestBody(required = false) String body
            ) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // hard coded first for testing
        String baseUrl = "https://" + api;
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
