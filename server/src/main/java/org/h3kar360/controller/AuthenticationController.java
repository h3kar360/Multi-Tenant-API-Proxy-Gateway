package org.h3kar360.controller;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.h3kar360.dto.LoginInputDto;
import org.h3kar360.dto.LoginResponseDto;
import org.h3kar360.dto.SignUpInputDto;
import org.h3kar360.dto.VerifyClientDto;
import org.h3kar360.model.Client;
import org.h3kar360.security.ClientUserDetails;
import org.h3kar360.service.AuthenticationService;
import org.h3kar360.service.JwtService;
import org.h3kar360.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LoginResponseDto> logIn(@RequestBody LoginInputDto loginInputDto) {
        Client authenticatedClient = authenticationService.authenticate(loginInputDto);

        String jwtToken = jwtService.generateToken(new ClientUserDetails(authenticatedClient));
        String refreshToken = refreshTokenService.generateRefreshToken();

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        LoginResponseDto loginResponseDto = new LoginResponseDto(jwtToken, refreshToken, jwtService.getJwtExpiration());

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(loginResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh() {
        return ResponseEntity.ok("soon");
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
}
