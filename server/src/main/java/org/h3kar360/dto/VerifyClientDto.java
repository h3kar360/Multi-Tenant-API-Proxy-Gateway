package org.h3kar360.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyClientDto {
    @NotBlank(message = "email must not be blank")
    private String email;

    @NotBlank(message = "verification code must not be blank")
    private String verificationCode;
}
