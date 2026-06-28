package org.h3kar360.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ProxyRequestDto;
import org.h3kar360.service.ProxyService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/proxy/v1/gateway")
@RequiredArgsConstructor
public class ProxyController {
    private final ProxyService proxyService;

    @RequestMapping("/{apiName}/**")
    public ResponseEntity<byte[]> proxyGatewayRequest(
            @PathVariable String apiName,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body
            ) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        ProxyRequestDto proxyRequest = ProxyRequestDto.builder()
                .apiName(apiName)
                .method(method)
                .request(request)
                .body(body)
                .build();

        return proxyService.forwardRequest(proxyRequest);
    }

}
