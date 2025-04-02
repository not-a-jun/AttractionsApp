package com.AstonProgect.exception;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Стандартный формат ответа об ошибке, отправляемый клиенту при возникновении исключений.
 * Этот класс содержит информацию о времени возникновения ошибки, HTTP-статусе,
 * типе ошибки, сообщении об ошибке и пути запроса.
 */
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
