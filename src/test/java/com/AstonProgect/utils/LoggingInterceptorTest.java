package com.AstonProgect.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    void preHandle_ShouldReturnTrue() throws Exception {
        // Arrange
        Enumeration<String> emptyHeaders = Collections.emptyEnumeration();

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/api"));
        when(request.getHeaderNames()).thenReturn(emptyHeaders);

        // Act
        boolean result = loggingInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(request).getMethod();
        verify(request).getRequestURL();
        verify(request).getHeaderNames();
    }

    @Test
    void preHandle_ShouldLogRequestWithHeaders() throws Exception {
        // Arrange
        Enumeration<String> headers = Collections.enumeration(Collections.singleton("Content-Type"));

        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/api"));
        when(request.getHeaderNames()).thenReturn(headers);
        when(request.getHeader("Content-Type")).thenReturn("application/json");

        // Act
        boolean result = loggingInterceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        verify(request).getHeader("Content-Type");
    }

    @Test
    void afterCompletion_ShouldCompleteWithoutErrors() throws Exception {
        // Arrange
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/api"));
        when(response.getStatus()).thenReturn(200);

        // Act & Assert
        loggingInterceptor.afterCompletion(request, response, new Object(), null);
        verify(response).getStatus();
    }

    @Test
    void afterCompletion_ShouldHandleExceptions() throws Exception {
        // Arrange
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com/api"));
        when(response.getStatus()).thenReturn(500);
        Exception testException = new RuntimeException("Test exception");

        // Act & Assert
        loggingInterceptor.afterCompletion(request, response, new Object(), testException);
        verify(response).getStatus();
    }
}