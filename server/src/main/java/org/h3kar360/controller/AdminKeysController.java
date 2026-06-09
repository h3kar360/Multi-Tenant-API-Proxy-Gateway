package org.h3kar360.controller;

import lombok.RequiredArgsConstructor;
import org.h3kar360.service.AdminService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/keys")
@RequiredArgsConstructor
public class AdminKeysController {
    private final AdminService adminService;

    @PostMapping
    public Mono<String> getProxyKey(@RequestHeader("x-user-id") String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);

        return adminService.generateProxyKey(userId);
    }
}
