package com.AstonProgect.repository;

import com.AstonProgect.AstonProjectApplication;
import com.AstonProgect.config.TestcontainersConfig;
import com.AstonProgect.model.Service;
import com.AstonProgect.model.ServiceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AstonProjectApplication.class, TestcontainersConfig.class})
@ActiveProfiles("integration-test")
@Transactional
@Import(TestcontainersConfig.class)
public class ServiceRepositoryIntegrationTest {

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void shouldSaveAndFindService() {
        // Arrange
        Service service = new Service();
        service.setName("Audio Guide");
        service.setDescription("Test Description");
        service.setServiceType(ServiceType.GUIDE);
        service.setPrice(BigDecimal.valueOf(10));

        // Act
        Service saved = serviceRepository.save(service);
        Service found = serviceRepository.findById(saved.getId()).orElseThrow();

        // Assert
        assertEquals("Audio Guide", found.getName());
        assertEquals(ServiceType.GUIDE, found.getServiceType());
    }

    @Test
    void shouldFindByServiceType() {
        // Arrange
        Service guide = new Service();
        guide.setName("Guide");
        guide.setDescription("Test Description");
        guide.setServiceType(ServiceType.GUIDE);
        guide.setPrice(BigDecimal.valueOf(199.99));
        serviceRepository.save(guide);

        Service food = new Service();
        food.setName("Restaurant");
        food.setDescription("Test Description");
        food.setServiceType(ServiceType.FOOD);
        food.setPrice(BigDecimal.valueOf(50.00));
        serviceRepository.save(food);

        // Act
        Page<Service> result = serviceRepository.findByServiceType(
                ServiceType.GUIDE, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Guide", result.getContent().get(0).getName());
    }
}
