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
 * Сервис для управления достопримечательностями (Attractions).
 * <p>
 * Этот сервис обеспечивает бизнес-логику для операций с достопримечательностями, включая:
 * <ul>
 *   <li>Создание новых достопримечательностей</li>
 *   <li>Получение информации о достопримечательностях</li>
 *   <li>Обновление существующих достопримечательностей</li>
 *   <li>Удаление достопримечательностей</li>
 *   <li>Поиск достопримечательностей по различным критериям</li>
 * </ul>
 * <p>
 * Все операции выполняются в транзакциях и имеют логирование для отслеживания действий.
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

    @Transactional(readOnly = true)
    public AttractionDto getAttractionById(UUID id) {
        log.debug("Fetching attraction with id: {}", id);
        return attractionRepository.findById(id)
                .map(attractionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<AttractionDto> getAllAttractions(Pageable pageable) {
        log.info("Fetching all attractions with pagination");
        return attractionRepository.findAll(pageable)
                .map(attractionMapper::toDto);
    }

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

    @Transactional
    public void deleteAttraction(UUID id) {
        log.info("Deleting attraction with id: {}", id);
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attraction not found with id: " + id);
        }
        attractionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<AttractionDto> searchAttractions(String name, AttractionType type, String city, Pageable pageable) {
        log.info("Searching attractions with name={}, type={}, city={}", name, type, city);
        return attractionRepository.searchAttractions(name, type, city, pageable)
                .map(attractionMapper::toDto);
    }

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