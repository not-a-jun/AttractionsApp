# AstonProject

Spring Boot приложение для хранения данных о достопримечательностях, их адресах и предоставляемых услугах.

## 📌 О проекте

Проект представляет собой RESTful API для управления информацией о достопримечательностях, их услугах и билетах.

## 🛠 Технологии
- **Java 17**
- **Spring Boot 3.2.5**
- **PostgreSQL**
- **Liquibase** (для миграций БД)
- **Spring Data JPA**
- **Spring Doc OpenAPI** (документация API)
- **Spring Actuator** (мониторинг)

## 🚀 Запуск проекта

### Требования
- JDK 17+
- PostgreSQL 15+
- Maven 3.9+

### Установка
1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/not-a-jun/AttractionsApp.git
2. Собрать проект:
   mvn clean install
3. Запустить приложение
   mvn spring-boot:run

   📚 Документация API
После запуска приложения документация доступна по адресу:

Swagger UI: http://localhost:8082/swagger-ui.html

OpenAPI JSON: http://localhost:8082/v3/api-docs

⚙️ Actuator Endpoints
Мониторинг приложения доступен через:
http://localhost:8082/actuator
