package com.AstonProgect.controller;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.service.TicketInfoService;
import io.swagger.v3.oas.annotations.Operation;
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
 * REST-контроллер для управления информацией о билетах.
 * <p>
 * Предоставляет API для выполнения операций CRUD с информацией о билетах, включая:
 * <ul>
 *   <li>Создание, обновление и удаление записей о билетах.</li>
 *   <li>Получение информации о билетах по ID или списком.</li>
 *   <li>Поиск билетов, связанных с конкретной достопримечательностью.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket", description = "API для управления информацией о билетах")
public class TicketInfoController {

    private final TicketInfoService ticketInfoService;

    /**
     * Создает новую запись о билетах.
     *
     * @param ticketInfoDto DTO с данными о билетах.
     * @return DTO созданной записи о билетах с HTTP-статусом 201.
     */
    @PostMapping
    @Operation(summary = "Создание информации о билетах", description = "Создает новую запись о билетах")
    @ApiResponse(responseCode = "201", description = "Информация о билетах успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    public ResponseEntity<TicketInfoDto> createTicketInfo(@Valid @RequestBody TicketInfoDto ticketInfoDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketInfoService.createTicketInfo(ticketInfoDto));
    }

    /**
     * Получает информацию о билетах по идентификатору.
     *
     * @param id UUID билета.
     * @return DTO с информацией о билетах.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение информации о билетах по ID", description = "Возвращает информацию о билетах с указанным ID")
    @ApiResponse(responseCode = "200", description = "Информация найдена")
    @ApiResponse(responseCode = "404", description = "Информация не найдена")
    public ResponseEntity<TicketInfoDto> getTicketInfoById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketInfoService.getTicketInfoById(id));
    }

    /**
     * Получает список всей информации о билетах с поддержкой пагинации.
     *
     * @param pageable Параметры пагинации (размер страницы, сортировка).
     * @return Страница с DTO информации о билетах.
     */
    @GetMapping
    @Operation(summary = "Получение всей информации о билетах", description = "Возвращает список всей информации о билетах")
    @ApiResponse(responseCode = "200", description = "Список информации успешно получен")
    public ResponseEntity<Page<TicketInfoDto>> getAllTicketInfos(
            @PageableDefault(size = 10, sort = "price") Pageable pageable) {
        return ResponseEntity.ok(ticketInfoService.getAllTicketInfos(pageable));
    }

    /**
     * Обновляет информацию о билетах.
     *
     * @param id UUID билета.
     * @param ticketInfoDto DTO с обновленными данными.
     * @return DTO обновленной записи о билетах.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновление информации о билетах", description = "Обновляет информацию о билетах с указанным ID")
    @ApiResponse(responseCode = "200", description = "Информация успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Информация не найдена")
    public ResponseEntity<TicketInfoDto> updateTicketInfo(
            @PathVariable UUID id,
            @Valid @RequestBody TicketInfoDto ticketInfoDto) {
        return ResponseEntity.ok(ticketInfoService.updateTicketInfo(id, ticketInfoDto));
    }

    /**
     * Удаляет информацию о билетах по идентификатору.
     *
     * @param id UUID билета.
     * @return HTTP-статус 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление информации о билетах", description = "Удаляет информацию о билетах с указанным ID")
    @ApiResponse(responseCode = "204", description = "Информация успешно удалена")
    @ApiResponse(responseCode = "404", description = "Информация не найдена")
    public ResponseEntity<Void> deleteTicketInfo(@PathVariable UUID id) {
        ticketInfoService.deleteTicketInfo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает информацию о билетах, связанных с достопримечательностью.
     *
     * @param attractionId UUID достопримечательности.
     * @param pageable Параметры пагинации.
     * @return Страница с DTO информации о билетах.
     */
    @GetMapping("/attraction/{attractionId}")
    @Operation(summary = "Получение информации о билетах по ID достопримечательности", description = "Возвращает билеты, связанные с указанной достопримечательностью")
    @ApiResponse(responseCode = "200", description = "Список билетов успешно получен")
    @ApiResponse(responseCode = "404", description = "Достопримечательность не найдена")
    public ResponseEntity<Page<TicketInfoDto>> getByAttractionId(
            @PathVariable UUID attractionId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ticketInfoService.getByAttractionId(attractionId, pageable));
    }
}
