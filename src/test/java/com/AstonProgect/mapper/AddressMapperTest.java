package com.AstonProgect.mapper;

import com.AstonProgect.dto.AddressDto;
import com.AstonProgect.model.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AddressMapperTest {

    private final AddressMapper mapper = new AddressMapperImpl();

    @Test
    void testToEntity_WithFullData() {
        AddressDto dto = new AddressDto();
        dto.setCity("Test City");
        dto.setStreet("Test Street");
        dto.setBuilding(123);
        dto.setRegion("Test Region");
        dto.setLongitude(12.34);
        dto.setLatitude(56.78);

        Address entity = mapper.toEntity(dto);

        assertAll(
                () -> assertEquals(dto.getId(), entity.getId()),
                () -> assertEquals(dto.getCity(), entity.getCity()),
                () -> assertEquals(dto.getStreet(), entity.getStreet()),
                () -> assertEquals(dto.getBuilding(), entity.getBuilding()),
                () -> assertEquals(dto.getRegion(), entity.getRegion()),
                () -> assertEquals(dto.getLongitude(), entity.getLongitude()),
                () -> assertEquals(dto.getLatitude(), entity.getLatitude())
        );
    }

    @ParameterizedTest
    @NullSource
    void testToEntity_WithNull(AddressDto nullDto) {
        assertNull(mapper.toEntity(nullDto));
    }

    @Test
    void testToDto_WithFullData() {
        Address entity = new Address();
        entity.setId(UUID.randomUUID());
        entity.setCity("Test City");
        entity.setStreet("Test Street");
        entity.setBuilding(123);
        entity.setRegion("Test Region");
        entity.setLongitude(12.34);
        entity.setLatitude(56.78);

        AddressDto dto = mapper.toDto(entity);

        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getCity(), dto.getCity()),
                () -> assertEquals(entity.getStreet(), dto.getStreet()),
                () -> assertEquals(entity.getBuilding(), dto.getBuilding()),
                () -> assertEquals(entity.getRegion(), dto.getRegion()),
                () -> assertEquals(entity.getLongitude(), dto.getLongitude()),
                () -> assertEquals(entity.getLatitude(), dto.getLatitude())
        );
    }

    @ParameterizedTest
    @NullSource
    void testToDto_WithNull(Address nullEntity) {
        assertNull(mapper.toDto(nullEntity));
    }

    @Test
    void testUpdateAddressFromDto_PartialUpdate() {
        Address entity = new Address();
        entity.setCity("Old City");
        entity.setStreet("Old Street");

        AddressDto dto = new AddressDto();
        dto.setCity("New City");
        dto.setRegion("New Region");

        mapper.updateAddressFromDto(dto, entity);

        assertAll(
                () -> assertEquals("New City", entity.getCity()),
                () -> assertEquals("Old Street", entity.getStreet()),
                () -> assertEquals("New Region", entity.getRegion())
        );
    }

    @Test
    void testUpdateAddressFromDto_WithNullFields() {
        Address entity = new Address();
        entity.setCity("Old City");
        entity.setStreet("Old Street");

        AddressDto dto = new AddressDto();

        mapper.updateAddressFromDto(dto, entity);

        assertAll(
                () -> assertEquals("Old City", entity.getCity()),
                () -> assertEquals("Old Street", entity.getStreet())
        );
    }

    @Test
    void testUpdateAddressFromDto_WithNullAttractions() {
        // Подготовка
        AddressDto dto = new AddressDto();
        Address entity = new Address();
        entity.setAttractions(null); // Явно устанавливаем null

        // Выполнение
        mapper.updateAddressFromDto(dto, entity);

        // Проверка
        assertNull(entity.getAttractions());
    }
}
