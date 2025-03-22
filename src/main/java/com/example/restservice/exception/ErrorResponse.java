package com.example.restservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Сущность ошибки")
public class ErrorResponse {
    @Schema(description = "Время возникновения ошибки",
            example = "2025-03-22T23:25:54.9462762", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime timestamp;
    @Schema(description = "Описание ошибки",
            example = "Пользователь с id 22 не найден", accessMode = Schema.AccessMode.READ_ONLY)
    private String message;

    public ErrorResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

}