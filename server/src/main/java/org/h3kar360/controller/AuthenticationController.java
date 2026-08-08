package org.h3kar360.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.*;
import org.h3kar360.model.Client;
import org.h3kar360.security.ClientUserDetails;
import org.h3kar360.service.AuthenticationService;
import org.h3kar360.service.JwtService;
import org.h3kar360.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<Client> signUp(@RequestBody SignUpInputDto signUpInputDto) {
        Client signedUpClient = authenticationService.signup(signUpInputDto);
        return ResponseEntity.ok(signedUpClient);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> logIn(
            @RequestBody LoginInputDto loginInputDto,
            HttpServletResponse response
        ) {
        Client authenticatedClient = authenticationService.authenticate(loginInputDto);

        String jwtToken = jwtService.generateToken(new ClientUserDetails(authenticatedClient));
        String refreshToken = refreshTokenService.generateRefreshToken(authenticatedClient);

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, refreshToken, jwtService.getJwtExpiration());

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyClient(@RequestBody VerifyClientDto verifyClientDto) {
        try {
            authenticationService.verifyClient(verifyClientDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request
    ) {
        String refreshToken = getRefreshToken(request);

        if(refreshToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing refresh token");
        }

        Client client = refreshTokenService.validateAndGetClient(refreshToken);
        if(client == null)
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token invalid or expired");

        String jwtToken = jwtService.generateToken(new ClientUserDetails(client));

        NewAccessTokenDto newAccessTokenDto = new NewAccessTokenDto();
        newAccessTokenDto.setNewAccessToken(jwtToken);

        return ResponseEntity.ok(newAccessTokenDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String refreshToken = getRefreshToken(request);

        if(refreshToken.isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing refresh token");
        }

        refreshTokenService.deleteRefreshToken(refreshToken);

        return ResponseEntity.ok("logged out");
    }

    public String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

            if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("refresh_token".equals(cookie.getName()))
                    return cookie.getValue();
            }
        }

        return "";
    }
}
