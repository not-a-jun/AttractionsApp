package com.AstonProgect.exception;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCorrectlySetAllFields() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setTimestamp(now);
        response.setStatus(404);
        response.setError("Not Found");
        response.setMessage("Resource not found");
        response.setPath("/api/resource");

        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
        assertEquals("/api/resource", response.getPath());
    }

    @Test
    void shouldCreateErrorResponseWithAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(now);
        response.setStatus(400);
        response.setError("Bad Request");
        response.setMessage("Validation failed");
        response.setPath("/api/endpoint");

        assertAll(
                () -> assertEquals(now, response.getTimestamp()),
                () -> assertEquals(400, response.getStatus()),
                () -> assertEquals("Bad Request", response.getError()),
                () -> assertEquals("Validation failed", response.getMessage()),
                () -> assertEquals("/api/endpoint", response.getPath())
        );
    }

    @Test
    void shouldHandleNullValuesInErrorResponse() {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(null);
        response.setPath(null);

        assertAll(
                () -> assertNull(response.getMessage()),
                () -> assertNull(response.getPath())
        );
    }

    @Test
    void shouldReturnCorrectToString() {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.of(2023, 1, 1, 0, 0));
        response.setStatus(404);
        response.setError("Not Found");

        String toString = response.toString();

        assertAll(
                () -> assertTrue(toString.contains("status=404")),
                () -> assertTrue(toString.contains("error=Not Found"))
        );
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse response1 = new ErrorResponse();
        response1.setTimestamp(now);
        response1.setStatus(404);
        response1.setError("Not Found");
        response1.setMessage("Resource not found");
        response1.setPath("/api/resource");

        ErrorResponse response2 = new ErrorResponse();
        response2.setTimestamp(now);
        response2.setStatus(404);
        response2.setError("Not Found");
        response2.setMessage("Resource not found");
        response2.setPath("/api/resource");

        // Проверка equals
        assertEquals(response1, response2);

        // Проверка hashCode
        assertEquals(response1.hashCode(), response2.hashCode());

        // Проверка canEqual
        assertTrue(response1.canEqual(response2));
    }

    @Test
    void testEqualsWithDifferentFields() {
        ErrorResponse response1 = new ErrorResponse();
        response1.setTimestamp(LocalDateTime.now());
        response1.setStatus(404);

        ErrorResponse response2 = new ErrorResponse();
        response2.setTimestamp(LocalDateTime.now().plusHours(1));
        response2.setStatus(500);

        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testEqualsWithNull() {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());

        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        ErrorResponse response = new ErrorResponse();
        assertNotEquals(response, new Object());
    }

    @Test
    void testHashCodeConsistency() {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(400);

        int initialHashCode = response.hashCode();
        assertEquals(initialHashCode, response.hashCode());
    }
}