package com.AstonProgect.exception;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Стандартный формат ответа об ошибке, отправляемый клиенту при возникновении исключений.
 * <p>
 * Содержит структурированную информацию об ошибке для единообразного формата ответов API.
 */
@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
