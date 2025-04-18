package com.AstonProgect.service;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.ServiceMapper;
import com.AstonProgect.model.Service;
import com.AstonProgect.model.ServiceType;
import com.AstonProgect.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Сервис для управления услугами.
 * <p>
 * Предоставляет полный набор операций для работы с услугами:
 * <ul>
 *   <li>CRUD-операции</li>
 *   <li>Получение всех типов услуг</li>
 *   <li>Поиск по различным критериям</li>
 *   <li>Фильтрация по типу</li>
 * </ul>
 */
@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    /**
     * Создает новую услугу.
     *
     * @param serviceDto DTO с данными услуги
     * @return DTO созданной услуги
     */
    public ServiceDto createService(ServiceDto serviceDto) {
        log.info("Creating new service: {}", serviceDto.getName());
        Service service = serviceMapper.toEntity(serviceDto);
        Service savedService = serviceRepository.save(service);
        return serviceMapper.toDto(savedService);
    }

    /**
     * Получает все возможные типы услуг.
     *
     * @return массив значений перечисления ServiceType
     */
    public ServiceType[] getAllServiceTypes() {
            return ServiceType.values();
    }

    /**
     * Получает услугу по ID.
     *
     * @param id UUID услуги
     * @return DTO услуги
     * @throws ResourceNotFoundException если услуга не найдена
     */
    @Transactional(readOnly = true)
    public ServiceDto getServiceById(UUID id) {
        log.debug("Fetching service with id: {}", id);
        return serviceRepository.findById(id)
                .map(serviceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }

    /**
     * Получает все услуги с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница с DTO услуг
     */
    @Transactional(readOnly = true)
    public Page<ServiceDto> getAllServices(Pageable pageable) {
        log.info("Fetching all services with pagination");
        return serviceRepository.findAll(pageable)
                .map(serviceMapper::toDto);
    }

    /**
     * Обновляет существующую услугу.
     *
     * @param id UUID обновляемой услуги
     * @param serviceDto DTO с новыми данными
     * @return DTO обновленной услуги
     * @throws ResourceNotFoundException если услуга не найдена
     */
    public ServiceDto updateService(UUID id, ServiceDto serviceDto) {
        log.info("Updating service with id: {}", id);
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        serviceMapper.updateServiceFromDto(serviceDto, existingService);
        Service updatedService = serviceRepository.save(existingService);
        return serviceMapper.toDto(updatedService);
    }

    /**
     * Удаляет услугу по ID.
     *
     * @param id UUID удаляемой услуги
     * @throws ResourceNotFoundException если услуга не найдена
     */
    public void deleteService(UUID id) {
        log.info("Deleting service with id: {}", id);
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found");
        }
        serviceRepository.deleteById(id);
    }

    /**
     * Получает услуги указанного типа.
     *
     * @param type тип услуги
     * @param pageable параметры пагинации
     * @return страница с DTO услуг
     */
    @Transactional(readOnly = true)
    public Page<ServiceDto> getServicesByType(ServiceType type, Pageable pageable) {
        log.info("Fetching services by type: {}", type);
        return serviceRepository.findByServiceType(type, pageable)
                .map(serviceMapper::toDto);
    }

    /**
     * Ищет услуги по названию и типу.
     *
     * @param name часть названия услуги (регистронезависимый поиск)
     * @param type тип услуги
     * @param pageable параметры пагинации
     * @return страница с найденными услугами
     */
    @Transactional(readOnly = true)
    public Page<ServiceDto> searchServices(String name, ServiceType type, Pageable pageable) {
        log.info("Searching services with name={}, type={}", name, type);
        return serviceRepository.searchServices(name, type, pageable)
                .map(serviceMapper::toDto);
    }
}