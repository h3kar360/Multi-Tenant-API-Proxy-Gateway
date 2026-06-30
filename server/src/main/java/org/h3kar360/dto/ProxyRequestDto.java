package org.h3kar360.dto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProxyRequestDto {
    @NotBlank(message = "api name cannot be blank")
    String apiName;

    @NotNull(message = "must have a request method")
    HttpMethod method;

    @NotNull(message = "api request required for all fields")
    HttpServletRequest request;

    byte[] body;
}
