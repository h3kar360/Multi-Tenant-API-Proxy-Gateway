package org.h3kar360.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputApiInfoDto {
    @NotBlank(message = "API name must not be blank")
    private String apiName;

    @NotBlank(message = "API URL must not be blank")
    private String apiUrl;

    @Min(value = 1000, message = "Connect timeout must be at least 1000ms")
    private Integer connectTimeout;

    @Min(value = 1000, message = "Read timeout must be at least 1000ms")
    private Integer readTimeout;
}
