package com.AstonProgect.controller;

import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.model.AttractionType;
import com.AstonProgect.service.AttractionService;
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
 * REST-контроллер для управления достопримечательностями.
 * <p>
 * Предоставляет API для выполнения операций CRUD с достопримечательностями, а также
 * дополнительные методы для поиска достопримечательностей по различным критериям.
 * <p>
 * Основные возможности:
 * <ul>
 *   <li>Создание новой достопримечательности</li>
 *   <li>Получение достопримечательности по ID</li>
 *   <li>Получение списка всех достопримечательностей</li>
 *   <li>Обновление информации о достопримечательности</li>
 *   <li>Удаление достопримечательности</li>
 *   <li>Получение списка достопримечательностей по городу</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
@Tag(name = "Attraction", description = "API для управления достопримечательностями")
public class AttractionController {

    private final AttractionService attractionService;

    /**
     * Создает новую достопримечательность.
     * <p>
     * Принимает DTO с данными достопримечательности, создает новую запись в системе
     * и возвращает созданную достопримечательность в формате DTO.
     *
     * @param attractionDto DTO с данными для создания достопримечательности
     * @return DTO созданной достопримечательности
     */
    @PostMapping
    @Operation(summary = "Создание достопримечательности", description = "Создает новую достопримечательность")
    @ApiResponse(responseCode = "201", description = "Достопримечательность успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    public ResponseEntity<AttractionDto> createAttraction(@Valid @RequestBody AttractionDto attractionDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attractionService.createAttraction(attractionDto));
    }

    /**
     * Получает достопримечательность по идентификатору.
     * <p>
     * Ищет в системе достопримечательность с указанным идентификатором.
     *
     * @param id Идентификатор достопримечательности
     * @return DTO достопримечательности
     * @throws ResourceNotFoundException если достопримечательность не найдена
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение достопримечательности по ID", description = "Возвращает достопримечательность с указанным ID")
    @ApiResponse(responseCode = "200", description = "Достопримечательность найдена")
    @ApiResponse(responseCode = "404", description = "Достопримечательность не найдена")
    public ResponseEntity<AttractionDto> getAttractionById(@PathVariable UUID id) {
        return ResponseEntity.ok(attractionService.getAttractionById(id));
    }

    /**
     * Получает список всех достопримечательностей.
     * <p>
     * Возвращает полный список всех достопримечательностей в системе.
     *
     * @return Список DTO с достопримечательностями
     */
    @GetMapping
    @Operation(summary = "Получение всех достопримечательностей", description = "Возвращает список всех достопримечательностей")
    @ApiResponse(responseCode = "200", description = "Список достопримечательностей успешно получен")
    public ResponseEntity<Page<AttractionDto>> getAllAttractions(
            @Parameter(description = "Параметры пагинации")
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(attractionService.getAllAttractions(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск достопримечательностей")
    @ApiResponse(responseCode = "200", description = "Результаты поиска")
    public ResponseEntity<Page<AttractionDto>> searchAttractions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) AttractionType type,
            @RequestParam(required = false) String city,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(attractionService.searchAttractions(name, type, city, pageable));
    }
}
