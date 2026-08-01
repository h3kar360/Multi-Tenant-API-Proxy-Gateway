package org.h3kar360.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.h3kar360.dto.ApiErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiErrorMessage> handleRateLimit(RateLimitException rateLimitException, HttpServletRequest request) {
        ApiErrorMessage errorMessage = rateLimitException.apiErrorMessage(request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-After-Seconds", "60")
                .body(errorMessage);
    }
}
