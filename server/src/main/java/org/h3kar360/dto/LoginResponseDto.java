package org.h3kar360.dto;

import lombok.*;

@Builder
@Value
public class LoginResponseDto {
    String clientName;
    String accessToken;
    String refreshToken;
    long expiresIn;
}
