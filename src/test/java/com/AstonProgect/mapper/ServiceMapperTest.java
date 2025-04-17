package com.AstonProgect.mapper;

import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.model.Service;
import com.AstonProgect.model.ServiceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceMapperTest {

    private final ServiceMapper mapper = new ServiceMapperImpl();

    @Test
    void testToEntity_WithFullData() {
        ServiceDto dto = new ServiceDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Test Service");
        dto.setDescription("Test Description");
        dto.setServiceType(ServiceType.GUIDE);
        dto.setPrice(new BigDecimal("100.50"));
        dto.setCurrency("USD");
        dto.setAvailability(true);

        Service entity = mapper.toEntity(dto);

        assertAll(
                () -> assertEquals(dto.getId(), entity.getId()),
                () -> assertEquals(dto.getName(), entity.getName()),
                () -> assertEquals(dto.getDescription(), entity.getDescription()),
                () -> assertEquals(dto.getServiceType(), entity.getServiceType()),
                () -> assertEquals(dto.getPrice(), entity.getPrice()),
                () -> assertEquals(dto.getCurrency(), entity.getCurrency()),
                () -> assertEquals(dto.isAvailability(), entity.isAvailability())
        );
    }

    @ParameterizedTest
    @NullSource
    void testToEntity_WithNull(ServiceDto nullDto) {
        assertNull(mapper.toEntity(nullDto));
    }

    @Test
    void testToDto_WithFullData() {
        Service entity = new Service();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Service");
        entity.setDescription("Test Description");
        entity.setServiceType(ServiceType.FOOD);
        entity.setPrice(new BigDecimal("200.75"));
        entity.setCurrency("EUR");
        entity.setAvailability(false);

        ServiceDto dto = mapper.toDto(entity);

        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getName(), dto.getName()),
                () -> assertEquals(entity.getDescription(), dto.getDescription()),
                () -> assertEquals(entity.getServiceType(), dto.getServiceType()),
                () -> assertEquals(entity.getPrice(), dto.getPrice()),
                () -> assertEquals(entity.getCurrency(), dto.getCurrency()),
                () -> assertEquals(entity.isAvailability(), dto.isAvailability())
        );
    }
    @ParameterizedTest
    @NullSource
    void testToDto_WithNull(Service nullEntity) {
        assertNull(mapper.toDto(nullEntity));
    }

    @Test
    void testUpdateServiceFromDto_PartialUpdate() {
        Service entity = new Service();
        entity.setName("Old Name");
        entity.setPrice(new BigDecimal("50.00"));

        ServiceDto dto = new ServiceDto();
        dto.setName("New Name");
        dto.setServiceType(ServiceType.TRANSPORT);

        mapper.updateServiceFromDto(dto, entity);

        assertAll(
                () -> assertEquals("New Name", entity.getName()),
                () -> assertEquals(new BigDecimal("50.00"), entity.getPrice()),
                () -> assertEquals(ServiceType.TRANSPORT, entity.getServiceType())
        );
    }

    @Test
    void testUpdateServiceFromDto_WithNullFields() {
        Service entity = new Service();
        entity.setName("Old Name");
        entity.setDescription("Old Description");

        ServiceDto dto = new ServiceDto();

        mapper.updateServiceFromDto(dto, entity);

        assertAll(
                () -> assertEquals("Old Name", entity.getName()),
                () -> assertEquals("Old Description", entity.getDescription())
        );
    }

    @Test
    void testUpdateServiceFromDto_WithNullAttractions() {
        // Подготовка
        ServiceDto dto = new ServiceDto();
        Service entity = new Service();
        entity.setAttractions(null); // Явно устанавливаем null

        // Выполнение
        mapper.updateServiceFromDto(dto, entity);

        // Проверка
        assertNull(entity.getAttractions());
    }
}