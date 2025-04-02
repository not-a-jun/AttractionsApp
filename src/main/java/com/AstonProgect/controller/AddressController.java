package com.AstonProgect.controller;

import com.AstonProgect.dto.AddressDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.service.AddressService;
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
 * REST-контроллер для управления адресами.
 * <p>
 * Предоставляет API для выполнения операций CRUD с адресами, а также
 * дополнительные методы для поиска адресов по различным критериям.
 * <p>
 * Основные возможности:
 * <ul>
 *   <li>Создание нового адреса</li>
 *   <li>Получение адреса по ID</li>
 *   <li>Получение списка всех адресов</li>
 *   <li>Обновление адреса</li>
 *   <li>Удаление адреса</li>
 *   <li>Поиск адресов по городу</li>
 *   <li>Поиск адресов по региону</li>
 *   <li>Поиск адресов по улице</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Address", description = "API для управления адресами")
public class AddressController {

    private final AddressService addressService;

    /**
     * Создает новый адрес.
     * <p>
     * Принимает DTO с данными адреса, создает новую запись в системе
     * и возвращает созданный адрес в формате DTO.
     *
     * @param addressDto DTO с данными для создания адреса
     * @return DTO созданного адреса
     */
    @PostMapping
    @Operation(summary = "Создание адреса", description = "Создает новый адрес")
    @ApiResponse(responseCode = "201", description = "Адрес успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.createAddress(addressDto));
    }

    /**
     * Получает адрес по идентификатору.
     * <p>
     * Ищет в системе адрес с указанным идентификатором.
     *
     * @param id Идентификатор адреса
     * @return DTO адреса
     * @throws ResourceNotFoundException если адрес не найден
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение адреса по ID", description = "Возвращает адрес с указанным ID")
    @ApiResponse(responseCode = "200", description = "Адрес найден")
    @ApiResponse(responseCode = "404", description = "Адрес не найден")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable UUID id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    /**
     * Получает список всех адресов.
     * <p>
     * Возвращает полный список всех адресов в системе.
     *
     * @return Список DTO адресов
     */
    @GetMapping
    @Operation(summary = "Получение всех адресов", description = "Возвращает список всех адресов")
    @ApiResponse(responseCode = "200", description = "Список адресов успешно получен")
    public ResponseEntity<Page<AddressDto>> getAllAddresses(
            @Parameter(description = "Параметры пагинации")
            @PageableDefault(size = 10, sort = "city") Pageable pageable) {
        return ResponseEntity.ok(addressService.getAllAddresses(pageable));
        }

    /**
     * Обновляет существующий адрес.
     * <p>
     * Принимает DTO с обновленными данными и идентификатор адреса,
     * обновляет соответствующую запись в системе.
     *
     * @param id Идентификатор обновляемого адреса
     * @param addressDto DTO с обновленными данными
     * @return DTO обновленного адреса
     * @throws ResourceNotFoundException если адрес не найден
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновление адреса", description = "Обновляет адрес с указанным ID")
    @ApiResponse(responseCode = "200", description = "Адрес успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Адрес не найден")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable UUID id,
            @Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.ok(addressService.updateAddress(id, addressDto));
    }

    /**
     * Удаляет адрес.
     * <p>
     * Удаляет запись адреса с указанным идентификатором из системы.
     *
     * @param id Идентификатор удаляемого адреса
     * @return Пустой ответ с HTTP статусом 204 (No Content)
     * @throws ResourceNotFoundException если адрес не найден
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление адреса", description = "Удаляет адрес с указанным ID")
    @ApiResponse(responseCode = "204", description = "Адрес успешно удален")
    @ApiResponse(responseCode = "404", description = "Адрес не найден")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск адресов по параметрам")
    @ApiResponse(responseCode = "200", description = "Результаты поиска")
    public ResponseEntity<Page<AddressDto>> searchAddresses(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String street,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(addressService.searchAddresses(city, region, street, pageable));
    }
}