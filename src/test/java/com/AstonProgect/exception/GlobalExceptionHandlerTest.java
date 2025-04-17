package com.AstonProgect.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        String errorMessage = "Attraction not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleNotFound(exception, request);

        assertErrorResponse(response, HttpStatus.NOT_FOUND, errorMessage);
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(new BeanPropertyBindingResult(new Object(), "object"));

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException(
                "Validation failed", Collections.emptySet());

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleConstraintViolation(exception, request);

        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Validation failed");
    }

    @Test
    void handleConstraintViolation_WithRealViolations() {
        // 1. Создаем Path для propertyPath
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("field1");

        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("field2");

        // 2. Создаем нарушения валидации
        Set<ConstraintViolation<?>> violations = Set.of(
                mockConstraintViolation(path1, "must not be null"),
                mockConstraintViolation(path2, "size must be between 1 and 10")
        );

        ConstraintViolationException exception =
                new ConstraintViolationException("Validation failed", violations);

        // 3. Вызываем обработчик
        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleConstraintViolation(exception, request);

        // 4. Проверяем результат
        String errorMessage = response.getBody().getMessage();
        assertAll(
                () -> assertTrue(errorMessage.contains("field1: must not be null")),
                () -> assertTrue(errorMessage.contains("field2: size must be between 1 and 10")),
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode())
        );
    }

    private ConstraintViolation<?> mockConstraintViolation(Path propertyPath, String message) {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn(message);
        return violation;
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturnConflict() {
        DataIntegrityViolationException exception =
                new DataIntegrityViolationException("Data integrity violation");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleDataIntegrityViolation(exception, request);

        assertErrorResponse(response, HttpStatus.CONFLICT, "Data integrity violation");
    }

    @Test
    void handleHttpMessageNotReadable_ShouldReturnBadRequest() {
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException("Invalid JSON");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleHttpMessageNotReadable(exception, request);

        assertErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid JSON");
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleAllExceptions(exception, request);

        assertErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private void assertErrorResponse(ResponseEntity<ErrorResponse> response,
                                     HttpStatus expectedStatus,
                                     String expectedMessage) {
        assertEquals(expectedStatus, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(expectedStatus.value(), body.getStatus());
        assertEquals(expectedStatus.getReasonPhrase(), body.getError());
        assertEquals(expectedMessage, body.getMessage());
        assertNotNull(body.getTimestamp());
        assertNotNull(body.getPath());
    }

    @Test
    void handleMethodArgumentNotValidException_WithFieldErrors() {
        // 1. Подготовка
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "object");

        // Используем addError() вместо registerFieldError
        bindingResult.addError(
                new FieldError("object", "name", "must not be blank")
        );
        bindingResult.addError(
                new FieldError("object", "price", "must be positive")
        );

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // 2. Выполнение
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidation(exception, request);

        // 3. Проверка
        String errorMessage = response.getBody().getMessage();
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertTrue(errorMessage.contains("name: must not be blank")),
                () -> assertTrue(errorMessage.contains("price: must be positive"))
        );
    }

    @Test
    void handleConstraintViolationException_WithEmptyViolations() {
        ConstraintViolationException exception = new ConstraintViolationException(Collections.emptySet());

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void buildErrorResponse_ShouldUseRequestPath() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/api/test");
        WebRequest request = new ServletWebRequest(mockRequest);

        Exception ex = new Exception("Test error");
        // Используем публичный handleAllExceptions вместо приватного buildErrorResponse
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAllExceptions(ex, request);

        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleDataIntegrityViolation_WithNullMessage() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(null);

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDataIntegrityViolation(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody().getMessage());
    }
}