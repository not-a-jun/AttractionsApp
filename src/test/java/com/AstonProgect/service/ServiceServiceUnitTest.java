package com.AstonProgect.service;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.ServiceMapper;
import com.AstonProgect.model.Service;
import com.AstonProgect.model.ServiceType;
import com.AstonProgect.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceUnitTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceDto serviceDto;
    private Service service;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        serviceDto = new ServiceDto();
        serviceDto.setId(testId);
        serviceDto.setName("Test Service");
        serviceDto.setDescription("Test Description");
        serviceDto.setServiceType(ServiceType.GUIDE);
        serviceDto.setPrice(BigDecimal.valueOf(100));
        serviceDto.setCurrency("USD");
        serviceDto.setAvailability(true);

        service = new Service();
        service.setId(testId);
        service.setName("Test Service");
    }

    @Test
    void createService_ShouldReturnCreatedService() {
        when(serviceMapper.toEntity(serviceDto)).thenReturn(service);
        when(serviceRepository.save(service)).thenReturn(service);
        when(serviceMapper.toDto(service)).thenReturn(serviceDto);

        ServiceDto result = serviceService.createService(serviceDto);

        assertNotNull(result);
        assertEquals(serviceDto.getName(), result.getName());
    }

    @Test
    void getServicesByType_ShouldReturnFilteredServices() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Service> page = new PageImpl<>(List.of(service));

        when(serviceRepository.findByServiceType(ServiceType.GUIDE, pageable))
                .thenReturn(page);
        when(serviceMapper.toDto(service)).thenReturn(serviceDto);

        Page<ServiceDto> result = serviceService.getServicesByType(ServiceType.GUIDE, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllServices_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Service> page = new PageImpl<>(List.of(service));

        when(serviceRepository.findAll(pageable)).thenReturn(page);
        when(serviceMapper.toDto(service)).thenReturn(serviceDto);

        Page<ServiceDto> result = serviceService.getAllServices(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateService_ShouldUpdateFields() {
        ServiceDto updateDto = new ServiceDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Desc");
        updateDto.setServiceType(ServiceType.FOOD);

        when(serviceRepository.findById(testId)).thenReturn(Optional.of(service));
        when(serviceRepository.save(service)).thenReturn(service);
        when(serviceMapper.toDto(service)).thenReturn(updateDto);

        ServiceDto result = serviceService.updateService(testId, updateDto);

        assertEquals("Updated Name", result.getName());
        assertEquals(ServiceType.FOOD, result.getServiceType());
    }

    @Test
    void getServiceById_ShouldReturnService() {
        when(serviceRepository.findById(testId)).thenReturn(Optional.of(service));
        when(serviceMapper.toDto(service)).thenReturn(serviceDto);

        ServiceDto result = serviceService.getServiceById(testId);

        assertEquals(serviceDto, result);
    }

    @Test
    void deleteService_ShouldSuccess() {
        when(serviceRepository.existsById(testId)).thenReturn(true);

        serviceService.deleteService(testId);

        verify(serviceRepository).deleteById(testId);
    }

    @Test
    void deleteService_ShouldThrowWhenNotFound() {
        when(serviceRepository.existsById(testId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> serviceService.deleteService(testId));
    }

    @Test
    void searchServices_ShouldReturnFiltered() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Service> page = new PageImpl<>(List.of(service));

        when(serviceRepository.searchServices(any(), any(), eq(pageable)))
                .thenReturn(page);
        when(serviceMapper.toDto(service)).thenReturn(serviceDto);

        Page<ServiceDto> result = serviceService.searchServices("test", ServiceType.GUIDE, pageable);

        assertEquals(1, result.getTotalElements());
    }
}
