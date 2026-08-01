package org.h3kar360.exception;

import org.h3kar360.dto.ApiErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException extends RuntimeException {
    public RateLimitException(final String message) {
        super(message);
    }

    public ApiErrorMessage apiErrorMessage(final String path) {
        return ApiErrorMessage.builder()
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error(HttpStatus.TOO_MANY_REQUESTS.name())
                .message(this.getMessage())
                .path(path)
                .build();
    }
}
