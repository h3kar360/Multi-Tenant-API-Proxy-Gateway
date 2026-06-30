package org.h3kar360.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDto {
    private Long id;
    private String apiName;
    private String apiUrl;
    private Integer connectTimeout;
    private Integer readTimeout;
}
