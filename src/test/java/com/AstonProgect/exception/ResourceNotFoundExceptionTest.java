package com.AstonProgect.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        String errorMessage = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage);

        assertEquals(errorMessage, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @NullAndEmptySource
    void shouldHandleBlankMessages(String message) {
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        if (message == null) {
            assertNull(exception.getMessage());
        } else {
            assertEquals(message, exception.getMessage());
        }
    }

    @Test
    void shouldPreserveCause() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception =
                new ResourceNotFoundException("Error", cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        String errorMessage = "Resource not found";
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(errorMessage, cause);

        assertAll(
                () -> assertEquals(errorMessage, exception.getMessage()),
                () -> assertEquals(cause, exception.getCause())
        );
    }

    @Test
    void shouldCreateExceptionWithCauseOnly() {
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(null, cause);

        assertAll(
                () -> assertNull(exception.getMessage()),
                () -> assertEquals(cause, exception.getCause())
        );
    }

    @Test
    void shouldHaveCorrectConstructorCount() {
        assertEquals(2, ResourceNotFoundException.class.getConstructors().length);
    }

    @Test
    void shouldBeRuntimeExceptionSubclass() {
        assertTrue(RuntimeException.class.isAssignableFrom(ResourceNotFoundException.class));
    }
}