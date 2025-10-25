package br.com.matheus161.linkai_api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDto (int status, String message, List<FieldError> errors, LocalDateTime timestamp) {
    public record FieldError(String field, String message) {}
}
