package com.AstonProgect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для OpenAPI (Swagger).
 * <p>
 * Настраивает документацию API, которая доступна по адресу:
 * {@code http://localhost:8081/swagger-ui.html}.
 * Документация включает описание API, контакты разработчиков и лицензию.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает основной компонент OpenAPI с информацией о сервисе,
     * контактах разработчика, лицензией.
     *
     * @return Сконфигурированный объект {@link OpenAPI} для генерации Swagger-документации.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AstonProject API")
                        .description("API для управления сервисами, билетами и достопримечательностями")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Aston Team")
                                .email("contact@astonproject.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
