package com.AstonProgect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 * <p>
 * Этот класс централизованно обрабатывает различные типы исключений, возникающие в приложении,
 * и преобразует их в структурированные HTTP-ответы с соответствующими кодами статуса.
 * Это позволяет клиентам получать понятные сообщения об ошибках.
 * <p>
 * Обрабатываемые типы исключений:
 * <ul>
 *   <li>ResourceNotFoundException - когда запрашиваемый ресурс не найден</li>
 *   <li>ConstraintViolationException - при нарушении ограничений валидации</li>
 *   <li>MethodArgumentNotValidException - при ошибках валидации аргументов метода</li>
 *   <li>DataIntegrityViolationException - при нарушении целостности данных</li>
 *   <li>HttpMessageNotReadableException - при проблемах с разбором JSON-запроса</li>
 *   <li>Exception - для всех других непредвиденных исключений</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение ResourceNotFoundException.
     * <p>
     * Возникает, когда запрашиваемый ресурс не найден в системе.
     * Возвращает статус 404 Not Found.
     *
     * @param ex исключение ResourceNotFoundException
     * @param request текущий веб-запрос
     * @return ResponseEntity с информацией об ошибке и статусом 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(new Exception(message), HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(status).body(response);
    }
}
