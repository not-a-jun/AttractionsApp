package com.AstonProgect.service;

import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.AttractionMapper;
import com.AstonProgect.model.*;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * Сервис для управления достопримечательностями.
 * <p>
 * Обеспечивает полный жизненный цикл работы с достопримечательностями:
 * <ul>
 *   <li>Создание с проверкой связанных сущностей</li>
 *   <li>Чтение с различными вариантами фильтрации</li>
 *   <li>Частичное и полное обновление</li>
 *   <li>Удаление с проверкой существования</li>
 * </ul>
 */
@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AttractionService {

    private final AttractionRepository attractionRepository;
    private final AddressRepository addressRepository;
    private final ServiceRepository serviceRepository;
    private final TicketInfoRepository ticketInfoRepository;
    private final AttractionMapper attractionMapper;

    /**
     * Создает новую достопримечательность.
     *
     * @param attractionDto DTO с данными для создания
     * @return DTO созданной достопримечательности
     * @throws ResourceNotFoundException если связанные сущности не найдены
     */
    @Transactional
    public AttractionDto createAttraction(AttractionDto attractionDto) {
        log.info("Creating attraction: {}", attractionDto.getName());

        // Проверка существования связанных сущностей
        if (attractionDto.getAddressId() != null && !addressRepository.existsById(attractionDto.getAddressId())) {
            throw new ResourceNotFoundException("Address not found with id: " + attractionDto.getAddressId());
        }

        if (attractionDto.getTicketInfoId() != null && !ticketInfoRepository.existsById(attractionDto.getTicketInfoId())) {
            throw new ResourceNotFoundException("TicketInfo not found with id: " + attractionDto.getTicketInfoId());
        }

        if (attractionDto.getServiceIds() != null) {
            attractionDto.getServiceIds().forEach(serviceId -> {
                if (!serviceRepository.existsById(serviceId)) {
                    throw new ResourceNotFoundException("Service not found with id: " + serviceId);
                }
            });
        }

        Attraction attraction = attractionMapper.toEntity(attractionDto);
        Attraction savedAttraction = attractionRepository.save(attraction);
        return attractionMapper.toDto(savedAttraction);
    }

    /**
     * Получает достопримечательность по ID.
     *
     * @param id UUID достопримечательности
     * @return DTO достопримечательности
     * @throws ResourceNotFoundException если достопримечательность не найдена
     */
    @Transactional(readOnly = true)
    public AttractionDto getAttractionById(UUID id) {
        log.debug("Fetching attraction with id: {}", id);
        return attractionRepository.findById(id)
                .map(attractionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with id: " + id));
    }

    /**
     * Получает все достопримечательности с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница с DTO достопримечательностей
     */
    @Transactional(readOnly = true)
    public Page<AttractionDto> getAllAttractions(Pageable pageable) {
        log.info("Fetching all attractions with pagination");
        return attractionRepository.findAll(pageable)
                .map(attractionMapper::toDto);
    }

    /**
     * Обновляет существующую достопримечательность.
     *
     * @param id UUID обновляемой достопримечательности
     * @param attractionDto DTO с новыми данными
     * @return DTO обновленной достопримечательности
     * @throws ResourceNotFoundException если достопримечательность или связанные сущности не найдены
     */
    @Transactional
    public AttractionDto updateAttraction(UUID id, AttractionDto attractionDto) {
        log.info("Updating attraction with id: {}", id);

        Attraction existingAttraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with id: " + id));

        // Проверка и обновление простых полей
        if (attractionDto.getName() != null) {
            existingAttraction.setName(attractionDto.getName());
        }
        if (attractionDto.getDescription() != null) {
            existingAttraction.setDescription(attractionDto.getDescription());
        }
        if (attractionDto.getAttractionType() != null) {
            existingAttraction.setAttractionType(attractionDto.getAttractionType());
        }

        // Обновление адреса
        if (attractionDto.getAddressId() != null) {
            Address address = addressRepository.findById(attractionDto.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + attractionDto.getAddressId()));
            existingAttraction.setAddress(address);
        }

        // Обновление услуг
        if (attractionDto.getServiceIds() != null && !attractionDto.getServiceIds().isEmpty()) {
            Set<Service> services = new HashSet<>();
            for (UUID serviceId : attractionDto.getServiceIds()) {
                Service service = serviceRepository.findById(serviceId)
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + serviceId));
                services.add(service);
            }
            existingAttraction.setServices(services);
        }

        // Обновление информации о билетах
        if (attractionDto.getTicketInfoId() != null) {
            TicketInfo ticketInfo = ticketInfoRepository.findById(attractionDto.getTicketInfoId())
                    .orElseThrow(() -> new ResourceNotFoundException("TicketInfo not found with id: " + attractionDto.getTicketInfoId()));
            existingAttraction.setTicketInfo(ticketInfo);
        }

        Attraction updatedAttraction = attractionRepository.save(existingAttraction);
        return attractionMapper.toDto(updatedAttraction);
    }

    /**
     * Удаляет достопримечательность по ID.
     *
     * @param id UUID удаляемой достопримечательности
     * @throws ResourceNotFoundException если достопримечательность не найдена
     */
    @Transactional
    public void deleteAttraction(UUID id) {
        log.info("Deleting attraction with id: {}", id);
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attraction not found with id: " + id);
        }
        attractionRepository.deleteById(id);
    }

    /**
     * Ищет достопримечательности по различным критериям.
     *
     * @param name часть названия (регистронезависимый поиск)
     * @param type тип достопримечательности
     * @param city город расположения
     * @param pageable параметры пагинации
     * @return страница с найденными достопримечательностями
     */
    @Transactional(readOnly = true)
    public Page<AttractionDto> searchAttractions(String name, AttractionType type, String city, Pageable pageable) {
        log.info("Searching attractions with name={}, type={}, city={}", name, type, city);
        return attractionRepository.searchAttractions(name, type, city, pageable)
                .map(attractionMapper::toDto);
    }

    /**
     * Получает достопримечательности, связанные с указанной услугой.
     *
     * @param serviceId UUID услуги
     * @param pageable параметры пагинации
     * @return страница с DTO достопримечательностей
     * @throws ResourceNotFoundException если услуга не найдена
     */
    @Transactional(readOnly = true)
    public Page<AttractionDto> getAttractionsByServiceId(UUID serviceId, Pageable pageable) {
        log.info("Fetching attractions by service id: {}", serviceId);
        if (!serviceRepository.existsById(serviceId)) {
            throw new ResourceNotFoundException("Service not found with id: " + serviceId);
        }
        return attractionRepository.findByServiceId(serviceId, pageable)
                .map(attractionMapper::toDto);
    }
}