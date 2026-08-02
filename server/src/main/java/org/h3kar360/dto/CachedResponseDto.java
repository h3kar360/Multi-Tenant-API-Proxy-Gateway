package org.h3kar360.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Builder
@Value
public class CachedResponseDto {
    int status;

    Map<String, String> headers;

    byte[] body;
}
