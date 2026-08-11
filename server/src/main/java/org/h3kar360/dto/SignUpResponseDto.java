package org.h3kar360.dto;

import lombok.Builder;
import lombok.Value;
import org.h3kar360.model.Client;

@Builder
@Value
public class SignUpResponseDto {
    Client client;
    String proxyKey;
}
