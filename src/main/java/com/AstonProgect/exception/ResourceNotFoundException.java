package com.AstonProgect.exception;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден в системе.
 * <p>
 * Это исключение используется сервисным слоем приложения для сигнализации о том,
 * что запрошенная сущность (например, Attraction, Address, TicketInfo и т.д.)
 * не существует в базе данных. Оно обрабатывается {@link GlobalExceptionHandler},
 * который преобразует его в HTTP-ответ со статусом 404 Not Found.
 * 
 * @see GlobalExceptionHandler#handleResourceNotFoundException(ResourceNotFoundException, org.springframework.web.context.request.WebRequest)
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
