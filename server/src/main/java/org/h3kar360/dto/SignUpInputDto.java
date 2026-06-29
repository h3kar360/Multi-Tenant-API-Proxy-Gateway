package org.h3kar360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpInputDto {
    @NotBlank(message = "username must not be blank")
    @Min(value = 3, message = "username must be more than 3 characters")
    private String username;

    @NotBlank(message = "email must not be blank")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Min(value = 8, message = "password must be more than 8 characters")
    private String password;
}
