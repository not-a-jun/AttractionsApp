package com.AstonProgect;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Класс для инициализации приложения при развертывании в традиционном сервлет-контейнере.
 * <p>
 * Наследует от {@link SpringBootServletInitializer} и переопределяет метод конфигурации
 * для запуска приложения в внешнем сервере приложений (например, Tomcat).
 * <p>
 * Требуется для развертывания WAR-файлов и обеспечивает:
 * <ul>
 *   <li>Совместимость с Servlet 3.0+ контейнерами</li>
 *   <li>Правильную инициализацию Spring Boot в сервлет-окружении</li>
 *   <li>Автоконфигурацию диспетчера сервлетов</li>
 * </ul>
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Конфигурирует приложение для запуска в сервлет-контейнере.
	 *
	 * @param application билдер Spring-приложения
	 * @return сконфигурированный билдер приложения
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AstonProjectApplication.class);
	}

}
