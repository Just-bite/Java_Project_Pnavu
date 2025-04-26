package com.example.restservice.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Error message entity")
public class ErrorResponse {
    @Schema(description = "Time when error occur",
            example = "2025-03-22T23:25:54.9462762", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime timestamp;
    @Schema(description = "Errors description",
            example = "User with id XX not found", accessMode = Schema.AccessMode.READ_ONLY)
    private String message;

    public ErrorResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

}