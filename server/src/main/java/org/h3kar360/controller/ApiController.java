package org.h3kar360.controller;

import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.ApiResponseDto;
import org.h3kar360.dto.InputApiInfoDto;
import org.h3kar360.dto.UpdateApiInfoDto;
import org.h3kar360.model.Api;
import org.h3kar360.model.Client;
import org.h3kar360.security.ClientUserDetails;
import org.h3kar360.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proxy/v1/api-info")
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @PostMapping()
    public ResponseEntity<ApiResponseDto> addApi(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody InputApiInfoDto inputApiInfoDto
    ) {
        ClientUserDetails clientUserDetails = (ClientUserDetails) userDetails;
        Client client = clientUserDetails.getClient();

        ApiResponseDto savedApi = apiService.addApi(client, inputApiInfoDto);

        return ResponseEntity.ok(savedApi);
    }

    @GetMapping()
    public ResponseEntity<List<ApiResponseDto>> getAllApis(@AuthenticationPrincipal UserDetails userDetails) {
        ClientUserDetails clientUserDetails = (ClientUserDetails) userDetails;
        long clientId = clientUserDetails.getClientId();

        List<ApiResponseDto> apis = apiService.getAllApi(clientId);

        return ResponseEntity.ok(apis);
    }

    @PutMapping("/{apiId}")
    public ResponseEntity<ApiResponseDto> updateApi(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long apiId,
            UpdateApiInfoDto updateApiInfoDto
    ) {
        ClientUserDetails clientUserDetails = (ClientUserDetails) userDetails;
        long clientId = clientUserDetails.getClientId();

        ApiResponseDto api = apiService.updateApi(clientId, apiId, updateApiInfoDto);

        return ResponseEntity.ok(api);
    }

    @DeleteMapping("/{apiId}")
    public ResponseEntity<String> deleteApi(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long apiId
    ) {
        ClientUserDetails clientUserDetails = (ClientUserDetails) userDetails;
        long clientId = clientUserDetails.getClientId();

        apiService.deleteApi(clientId, apiId);

        return ResponseEntity.ok("Successfully deleted " + apiId);
    }
}
