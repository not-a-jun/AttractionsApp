package com.AstonProgect;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Интеграционный тест для проверки корректности загрузки контекста приложения.
 * <p>
 * Этот тест проверяет, что контекст Spring успешно загружается,
 * что свидетельствует о правильной конфигурации приложения.
 * Использует тестовый профиль и отдельный файл свойств для тестирования.
 */
@SpringBootTest
@EntityScan("com.AstonProgect.model")
@ActiveProfiles("integration-test")
class AstonProjectApplicationTests {

	/**
	 * Проверяет успешную загрузку контекста Spring.
	 * <p>
	 * Если тест выполняется успешно, это означает, что все бины
	 * и компоненты приложения корректно настроены и инициализированы.
	 */
	@Test
	void contextLoads() {
		// простая проверка загрузки контекста
	}

}
