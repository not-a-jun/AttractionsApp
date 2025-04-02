package com.AstonProgect.controller;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.model.ServiceType;
import com.AstonProgect.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST-контроллер для управления услугами.
 * <p>
 * Предоставляет API для выполнения операций CRUD с услугами, а также
 * дополнительные методы для поиска услуг по различным критериям.
 * <p>
 * Основные возможности:
 * <ul>
 *   <li>Создание новой услуги</li>
 *   <li>Получение услуги по ID</li>
 *   <li>Получение списка всех услуг</li>
 *   <li>Обновление информации об услуге</li>
 *   <li>Удаление услуги</li>
 *   <li>Получение списка услуг по типу</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "Service", description = "API для управления услугами")
public class ServiceController {

    private final ServiceService serviceService;

    @PostMapping
    @Operation(summary = "Создание услуги", description = "Создает новую услугу")
    @ApiResponse(responseCode = "201", description = "Услуга успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceDto serviceDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceService.createService(serviceDto));
    }

    @GetMapping
    @Operation(summary = "Получение всех услуг", description = "Возвращает список всех услуг")
    @ApiResponse(responseCode = "200", description = "Список услуг успешно получен")
    public ResponseEntity<Page<ServiceDto>> getAllServices(
            @Parameter(description = "Параметры пагинации")
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(serviceService.getAllServices(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление услуги", description = "Обновляет услугу с указанным ID")
    @ApiResponse(responseCode = "200", description = "Услуга успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Услуга не найдена")
    public ResponseEntity<ServiceDto> updateService(
            @PathVariable UUID id,
            @Valid @RequestBody ServiceDto serviceDto) {
        return ResponseEntity.ok(serviceService.updateService(id, serviceDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление услуги", description = "Удаляет услугу с указанным ID")
    @ApiResponse(responseCode = "204", description = "Услуга успешно удалена")
    @ApiResponse(responseCode = "404", description = "Услуга не найдена")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/types")
    @Operation(summary = "Получение всех типов услуг")
    @ApiResponse(responseCode = "200", description = "Список типов услуг")
    public ResponseEntity<ServiceType[]> getAllServiceTypes() {
        return ResponseEntity.ok(ServiceType.values());
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск услуг по параметрам")
    public ResponseEntity<Page<ServiceDto>> searchServices(
            @RequestParam(required = false) ServiceType type,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(serviceService.searchServices(name, type, pageable));
    }
}
