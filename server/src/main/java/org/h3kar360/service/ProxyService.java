package org.h3kar360.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ProxyRequestDto;
import org.h3kar360.repository.ApiRepository;
import org.h3kar360.repository.ClientRepository;
import org.h3kar360.repository.projection.ApiInfoOnly;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ProxyService {
    private final ApiRepository apiRepository;
    private final ClientRepository clientRepository;

    // cache for rest clients based on timeouts
    ConcurrentHashMap<String, RestClient> restClientCache = new ConcurrentHashMap<>();

    public ResponseEntity<byte[]> forwardRequest(ProxyRequestDto proxyRequest) {
        // extracting all the fields of the dto
        final HttpServletRequest request = proxyRequest.getRequest();
        final HttpMethod method = proxyRequest.getMethod();
        final String apiName = proxyRequest.getApiName();
        final byte[] body = proxyRequest.getBody();
        final long clientId = proxyRequest.getClientId();

        // get base url
        ApiInfoOnly api = apiRepository.findApiInfoByApiNameAndClientId(apiName, clientId)
                .orElseThrow(() -> new RuntimeException("API not found"));

        String apiUrl = api.getApiUrl();
        int connectTimeout = api.getConnectTimeout() != null ? api.getConnectTimeout() : 30000;
        int readTimeout = api.getReadTimeout() != null ? api.getReadTimeout() : 30000;

        String cacheKey = "%d:%d".formatted(connectTimeout, readTimeout);

        // create the rest client with custom timeouts
        RestClient restClient = restClientCache.computeIfAbsent(cacheKey, k -> {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

            factory.setConnectTimeout(connectTimeout);
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
        String apiUri = path.replace("/proxy/v1/gateway/" + apiName, "");

        String targetUrl = query != null && !query.isEmpty()
                ? apiUrl + apiUri + "?" + query
                : apiUrl + apiUri;

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
