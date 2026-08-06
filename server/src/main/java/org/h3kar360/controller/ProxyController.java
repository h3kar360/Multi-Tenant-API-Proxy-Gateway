package org.h3kar360.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.h3kar360.annotation.RateLimitProtected;
import org.h3kar360.dto.ProxyRequestDto;
import org.h3kar360.model.Client;
import org.h3kar360.security.ClientUserDetails;
import org.h3kar360.service.ProxyManagerService;
import org.h3kar360.service.ProxyService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/proxy/v1/gateway")
@RequiredArgsConstructor
public class ProxyController {
    private final ProxyManagerService proxyManagerService;

    @RequestMapping("/{apiName}/**")
    @RateLimitProtected
    public ResponseEntity<byte[]> proxyGatewayRequest(
            @PathVariable String apiName,
            @RequestHeader("X-Proxy-Key") String proxyKey,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body
            ) {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        ProxyRequestDto proxyRequest = ProxyRequestDto.builder()
                .apiName(apiName)
                .method(method)
                .request(request)
                .body(body)
                .proxyKey(proxyKey)
                .build();

        return proxyManagerService.proxyManager(proxyRequest);
    }

}
