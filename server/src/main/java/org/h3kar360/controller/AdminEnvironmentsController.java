package org.h3kar360.controller;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.EnvironmentRequest;
import org.h3kar360.model.ExternalAPIKey;
import org.h3kar360.service.AdminService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/environments")
@RequiredArgsConstructor
public class AdminEnvironmentsController {
    private final AdminService adminService;

    @PostMapping
    public Flux<ExternalAPIKey> postEnvironmentVariables(@RequestBody List<EnvironmentRequest> reqs, @RequestHeader("x-user-id") String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);

        return adminService.insertEnvironments(reqs, userId);
    }
}
