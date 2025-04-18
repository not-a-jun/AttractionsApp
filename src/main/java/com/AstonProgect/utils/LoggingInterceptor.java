package com.AstonProgect.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Перехватчик для логирования HTTP-запросов и ответов.
 * <p>
 * Реализует интерфейс {@link HandlerInterceptor} для перехвата и логирования:
 * <ul>
 *   <li>Входящих HTTP-запросов (метод, URL, заголовки)</li>
 *   <li>Исходящих HTTP-ответов (статус код)</li>
 *   <li>Исключений, возникших во время обработки запроса</li>
 * </ul>
 * <p>
 * Логирование выполняется с использованием SLF4J с уровнем INFO для успешных запросов
 * и ERROR для исключений.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Логирует информацию о входящем HTTP-запросе перед его обработкой.
     * <p>
     * Записывает в лог:
     * <ul>
     *   <li>HTTP-метод (GET, POST и т.д.)</li>
     *   <li>Полный URL запроса</li>
     *   <li>Все заголовки запроса</li>
     * </ul>
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param handler выбранный обработчик запроса
     * @return всегда true (продолжить обработку запроса)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Headers: {}", Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));
        return true;
    }

    /**
     * Логирует информацию о завершении обработки запроса.
     * <p>
     * Записывает в лог:
     * <ul>
     *   <li>URL запроса</li>
     *   <li>HTTP-статус ответа</li>
     *   <li>Исключение (если возникло)</li>
     * </ul>
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param handler обработчик, использованный для запроса
     * @param ex исключение (null если исключений не было)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.info("Request completed: {}", request.getRequestURL());
        logger.info("Response Status: {}", response.getStatus());
        if (ex != null) {
            logger.error("Exception occurred: ", ex);
        }
    }
}
