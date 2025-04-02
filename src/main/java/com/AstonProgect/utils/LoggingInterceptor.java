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
 * Данный компонент реализует интерфейс {@link HandlerInterceptor} для перехвата
 * HTTP-запросов и ответов с целью их логирования. Он регистрирует информацию о
 * методе запроса, URL, заголовках, статусе ответа и возможных исключениях.
 * <p>
 * Перехватчик выполняет логирование в двух местах жизненного цикла запроса:
 * перед обработкой запроса и после завершения обработки запроса.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Метод, вызываемый перед обработкой запроса.
     * <p>
     * Регистрирует информацию о входящем HTTP-запросе, включая
     * метод запроса, URL и заголовки.
     *
     * @param request текущий HTTP-запрос
     * @param response текущий HTTP-ответ
     * @param handler выбранный обработчик для обработки запроса
     * @return true если процесс запроса должен продолжиться
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
     * Метод, вызываемый после завершения обработки запроса.
     * <p>
     * Регистрирует информацию о завершении обработки HTTP-запроса,
     * включая URL запроса, статус ответа и любые возникшие исключения.
     *
     * @param request текущий HTTP-запрос
     * @param response текущий HTTP-ответ
     * @param handler обработчик, который использовался для обработки запроса
     * @param ex исключение, возникшее во время обработки запроса, или null если исключений не было
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
