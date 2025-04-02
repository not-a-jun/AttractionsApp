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
 * 1. Настройку CORS для разрешения кросс-доменных запросов
 * 2. Логирование всех входящих HTTP-запросов к API
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Настройка CORS для разрешения запросов со всех источников
     * 
     * @param registry Реестр CORS-правил
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
    
    /**
     * Добавление интерцептора для логирования HTTP-запросов
     * 
     * @param registry Реестр интерцепторов
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor());
    }
    
    /**
     * Интерцептор для логирования HTTP-запросов
     */
    private static class RequestLoggingInterceptor implements HandlerInterceptor {
        private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
        
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
