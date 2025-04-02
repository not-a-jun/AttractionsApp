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
 * Предоставляет методы для выполнения бизнес-логики, связанной с услугами:
 * создание, получение, обновление, удаление и поиск по различным критериям.
 */
@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

        public ServiceDto createService(ServiceDto serviceDto) {
        log.info("Creating new service: {}", serviceDto.getName());
        Service service = serviceMapper.toEntity(serviceDto);
        Service savedService = serviceRepository.save(service);
        return serviceMapper.toDto(savedService);
    }

    public ServiceType[] getAllServiceTypes() {
            return ServiceType.values();
    }

    @Transactional(readOnly = true)
    public ServiceDto getServiceById(UUID id) {
        log.debug("Fetching service with id: {}", id);
        return serviceRepository.findById(id)
                .map(serviceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Page<ServiceDto> getAllServices(Pageable pageable) {
        log.info("Fetching all services with pagination");
        return serviceRepository.findAll(pageable)
                .map(serviceMapper::toDto);
    }

    public ServiceDto updateService(UUID id, ServiceDto serviceDto) {
        log.info("Updating service with id: {}", id);
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        serviceMapper.updateServiceFromDto(serviceDto, existingService);
        Service updatedService = serviceRepository.save(existingService);
        return serviceMapper.toDto(updatedService);
    }

    public void deleteService(UUID id) {
        log.info("Deleting service with id: {}", id);
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found");
        }
        serviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ServiceDto> getServicesByType(ServiceType type, Pageable pageable) {
        log.info("Fetching services by type: {}", type);
        return serviceRepository.findByServiceType(type, pageable)
                .map(serviceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ServiceDto> searchServices(String name, ServiceType type, Pageable pageable) {
        log.info("Searching services with name={}, type={}", name, type);
        return serviceRepository.searchServices(name, type, pageable)
                .map(serviceMapper::toDto);
    }
}