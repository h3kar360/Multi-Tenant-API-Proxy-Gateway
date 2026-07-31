package org.h3kar360.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ApiResponseDto {
    Long id;
    String apiName;
    String apiUrl;
    Integer connectTimeout;
    Integer readTimeout;
}
