package org.h3kar360.controller;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ProxyKeyResponseDto;
import org.h3kar360.model.Client;
import org.h3kar360.security.ClientUserDetails;
import org.h3kar360.service.ProxyKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("proxy/v1/key")
@RequiredArgsConstructor
public class ProxyKeyController {
    private final ProxyKeyService proxyKeyService;

    @PostMapping("generate")
    public ResponseEntity<ProxyKeyResponseDto> createKey(
            @AuthenticationPrincipal ClientUserDetails clientUserDetails
    ) {
        Client client = clientUserDetails.getClient();
        ProxyKeyResponseDto createProxyKey = proxyKeyService.createKey(client);
        return ResponseEntity.ok(createProxyKey);
    }

    @PostMapping("/regenerate")
    public ResponseEntity<ProxyKeyResponseDto> recreateKey(
            @AuthenticationPrincipal ClientUserDetails clientUserDetails
            ) {
        Client client = clientUserDetails.getClient();
        ProxyKeyResponseDto recreateProxyKey = proxyKeyService.recreateKey(client);
        return ResponseEntity.ok(recreateProxyKey);
    }
}
