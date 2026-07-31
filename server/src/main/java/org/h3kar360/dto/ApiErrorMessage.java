package org.h3kar360.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Value
public class ApiErrorMessage {
    UUID id = UUID.randomUUID();

    int status;

    String error;

    String message;

    LocalDateTime timeStamp = LocalDateTime.now(Clock.systemUTC());

    String path;
}
