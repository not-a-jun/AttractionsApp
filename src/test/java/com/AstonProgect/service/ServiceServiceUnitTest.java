package com.AstonProgect.service;

import com.AstonProgect.dto.ServiceDto;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
}
