package com.AstonProgect.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Конфигурация веб-приложения, которая обеспечивает:
 * <ol>
 *   <li>Настройку CORS для разрешения кросс-доменных запросов.</li>
 *   <li>Логирование всех входящих HTTP-запросов к API.</li>
 * </ol>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настройка CORS для разрешения запросов со всех источников.
     *
     * @param registry Реестр CORS-правил, куда добавляются разрешённые методы, заголовки и origins.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    /**
     * Добавление интерцептора для логирования HTTP-запросов.
     *
     * @param registry Реестр интерцепторов, куда регистрируется {@link RequestLoggingInterceptor}.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor());
    }

    /**
     * Внутренний интерцептор для логирования деталей HTTP-запросов и ответов.
     * <p>
     * Логирует:
     * <ul>
     *   <li>Входящие запросы (метод, URI, IP, заголовки).</li>
     *   <li>Статусы ответов.</li>
     * </ul>
     */
    private static class RequestLoggingInterceptor implements HandlerInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

        /**
         * Логирует детали входящего запроса перед его обработкой.
         *
         * @param request  HTTP-запрос.
         * @param response HTTP-ответ.
         * @param handler  Обработчик запроса.
         * @return {@code true} для продолжения выполнения цепочки интерцепторов.
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            String headers = Collections.list(request.getHeaderNames()).stream()
                    .map(headerName -> headerName + ": " + request.getHeader(headerName))
                    .collect(Collectors.joining(", "));
                    
            logger.info("REQUEST: {} {} {}, Headers: [{}]", 
                    request.getMethod(), 
                    request.getRequestURI() + queryString,
                    request.getRemoteAddr(),
                    headers);
            
            return true;
        }

        /**
         * Логирует статус ответа после завершения обработки запроса.
         *
         * @param request HTTP-запрос.
         * @param response HTTP-ответ.
         * @param handler Обработчик запроса.
         * @param ex Исключение, если возникло во время обработки.
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                Object handler, Exception ex) {
            logger.info("RESPONSE: {} {} => Status: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus());
        }
    }
}
