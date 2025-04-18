package com.AstonProgect.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST-контроллеров.
 * <p>
 * Перехватывает исключения, возникающие в контроллерах, и преобразует их в
 * стандартизированные HTTP-ответы с объектом {@link ErrorResponse}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения при отсутствии ресурса.
     *
     * @param ex Исключение {@link ResourceNotFoundException}.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 404 и деталями ошибки.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    /**
     * Обрабатывает ошибки валидации входных параметров методов.
     *
     * @param ex Исключение {@link MethodArgumentNotValidException}.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 400 и списком ошибок валидации.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(new Exception(message), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обрабатывает нарушения ограничений валидации.
     *
     * @param ex Исключение {@link ConstraintViolationException}.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 400 и списком нарушений.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String message = !ex.getConstraintViolations().isEmpty() ? ex.getConstraintViolations().stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining("; ")) : ex.getMessage();
        return buildErrorResponse(new Exception(message), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обрабатывает нарушения целостности данных.
     *
     * @param ex Исключение {@link DataIntegrityViolationException}.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    /**
     * Обрабатывает ошибки чтения тела запроса.
     *
     * @param ex Исключение {@link HttpMessageNotReadableException}.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обрабатывает все необработанные исключения.
     *
     * @param ex Произошедшее исключение.
     * @param request Объект запроса.
     * @return Ответ с HTTP-статусом 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Строит стандартный ответ об ошибке.
     *
     * @param ex Исключение.
     * @param status HTTP-статус.
     * @param request Объект запроса.
     * @return Ответ с объектом {@link ErrorResponse}.
     */
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