package com.AstonProgect.mapper;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.model.*;
import com.AstonProgect.repository.AttractionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketInfoMapperTest {

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private TicketInfoMapperImpl mapper;

    @Test
    void testToEntity_WithFullData() {
        UUID attractionId = UUID.randomUUID();
        Attraction attraction = new Attraction();
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));

        TicketInfoDto dto = new TicketInfoDto();
        dto.setId(UUID.randomUUID());
        dto.setAttractionId(attractionId);
        dto.setPrice(new BigDecimal("150.99"));
        dto.setCurrency("EUR");
        dto.setAvailability(true);

        TicketInfo entity = mapper.toEntity(dto);

        assertAll(
                () -> assertEquals(dto.getId(), entity.getId()),
                () -> assertEquals(dto.getPrice(), entity.getPrice()),
                () -> assertEquals(dto.getCurrency(), entity.getCurrency()),
                () -> assertEquals(dto.getAvailability(), entity.isAvailability()),
                () -> assertEquals(attraction, entity.getAttraction())
        );
    }

    @Test
    void testToEntity_WithNullAttractionId() {
        TicketInfoDto dto = new TicketInfoDto();
        dto.setAttractionId(null);

        TicketInfo entity = mapper.toEntity(dto);

        assertNull(entity.getAttraction());
    }

    @Test
    void testToEntity_AttractionNotFound() {
        UUID attractionId = UUID.randomUUID();
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.empty());

        TicketInfoDto dto = new TicketInfoDto();
        dto.setAttractionId(attractionId);

        assertThrows(RuntimeException.class, () -> mapper.toEntity(dto));
    }

    @ParameterizedTest
    @NullSource
    void testToEntity_WithNullDto(TicketInfoDto nullDto) {
        assertNull(mapper.toEntity(nullDto));
    }

    @Test
    void testToDto_WithFullData() {
        TicketInfo entity = new TicketInfo();
        entity.setId(UUID.randomUUID());
        entity.setPrice(new BigDecimal("200.50"));
        entity.setCurrency("USD");
        entity.setAvailability(false);

        Attraction attraction = new Attraction();
        attraction.setId(UUID.randomUUID());
        entity.setAttraction(attraction);

        TicketInfoDto dto = mapper.toDto(entity);

        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getPrice(), dto.getPrice()),
                () -> assertEquals(entity.getCurrency(), dto.getCurrency()),
                () -> assertEquals(entity.isAvailability(), dto.getAvailability()),
                () -> assertEquals(attraction.getId(), dto.getAttractionId())
        );
    }

    @Test
    void testToDto_WithNullAttraction() {
        TicketInfo entity = new TicketInfo();
        entity.setAttraction(null);

        TicketInfoDto dto = mapper.toDto(entity);

        assertNull(dto.getAttractionId());
    }

    @ParameterizedTest
    @NullSource
    void testToDto_WithNullEntity(TicketInfo nullEntity) {
        assertNull(mapper.toDto(nullEntity));
    }

    @Test
    void testUpdateTicketInfoFromDto() {
        TicketInfo entity = new TicketInfo();
        entity.setPrice(new BigDecimal("100.00"));
        entity.setCurrency("USD");

        TicketInfoDto dto = new TicketInfoDto();
        dto.setPrice(new BigDecimal("150.00"));
        dto.setAvailability(true);

        mapper.updateTicketInfoFromDto(dto, entity);

        assertAll(
                () -> assertEquals(new BigDecimal("150.00"), entity.getPrice()),
                () -> assertEquals("USD", entity.getCurrency()),
                () -> assertTrue(entity.isAvailability())
        );
    }

    @Test
    void testUpdateTicketInfoFromDto_WithNullFields() {
        TicketInfo entity = new TicketInfo();
        entity.setPrice(new BigDecimal("100.00"));
        entity.setCurrency("USD");

        TicketInfoDto dto = new TicketInfoDto();

        mapper.updateTicketInfoFromDto(dto, entity);

        assertAll(
                () -> assertEquals(new BigDecimal("100.00"), entity.getPrice()),
                () -> assertEquals("USD", entity.getCurrency())
        );
    }

    @Test
    void testUpdateTicketInfoFromDto_WithNullAttraction() {
        // Подготовка
        TicketInfoDto dto = new TicketInfoDto();
        dto.setAttractionId(null); // Нет привязки к достопримечательности
        TicketInfo entity = new TicketInfo();

        // Выполнение
        mapper.updateTicketInfoFromDto(dto, entity);

        // Проверка
        assertNull(entity.getAttraction());
    }

    @Test
    void testMapAttraction_NotFound() {
        UUID attractionId = UUID.randomUUID();
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mapper.mapAttraction(attractionId));
    }
}