package com.AstonProgect.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        String errorMessage = "Attraction not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleNotFound(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(new BeanPropertyBindingResult(new Object(), "object"));
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorResponse> response =
                exceptionHandler.handleValidation(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().getMessage());
    }
}