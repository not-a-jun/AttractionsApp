package com.AstonProgect.service;

import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.TicketInfoMapper;
import com.AstonProgect.model.TicketInfo;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.TicketInfoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketInfoServiceUnitTest {

    @Mock
    private TicketInfoRepository ticketInfoRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private TicketInfoMapper ticketInfoMapper;

    @InjectMocks
    private TicketInfoService ticketInfoService;

    private TicketInfoDto ticketInfoDto;
    private TicketInfo ticketInfo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        ticketInfoDto = new TicketInfoDto();
        ticketInfoDto.setId(testId);
        ticketInfoDto.setPrice(BigDecimal.valueOf(100));
        ticketInfoDto.setCurrency("USD");
        ticketInfoDto.setAvailability(true);
        ticketInfoDto.setAttractionId(UUID.randomUUID());

        ticketInfo = new TicketInfo();
        ticketInfo.setId(testId);
        ticketInfo.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void createTicketInfo_ShouldSuccess() {
        when(attractionRepository.existsById(ticketInfoDto.getAttractionId())).thenReturn(true);
        when(ticketInfoRepository.existsByAttraction_Id(ticketInfoDto.getAttractionId())).thenReturn(false);
        when(ticketInfoMapper.toEntity(ticketInfoDto)).thenReturn(ticketInfo);
        when(ticketInfoRepository.save(ticketInfo)).thenReturn(ticketInfo);
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(ticketInfoDto);

        TicketInfoDto result = ticketInfoService.createTicketInfo(ticketInfoDto);

        assertNotNull(result);
        assertEquals(ticketInfoDto.getPrice(), result.getPrice());
    }

    @Test
    void createTicketInfo_ShouldThrowWhenAttractionNotFound() {
        when(attractionRepository.existsById(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> ticketInfoService.createTicketInfo(ticketInfoDto));
    }

    @Test
    void createTicketInfo_ShouldThrowWhenTicketExists() {
        when(attractionRepository.existsById(any())).thenReturn(true);
        when(ticketInfoRepository.existsByAttraction_Id(any())).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> ticketInfoService.createTicketInfo(ticketInfoDto));
    }

    @Test
    void getByAttractionId_ShouldReturnTicketInfo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TicketInfo> page = new PageImpl<>(List.of(ticketInfo));

        when(attractionRepository.existsById(testId)).thenReturn(true);
        when(ticketInfoRepository.findByAttraction_Id(testId, pageable)).thenReturn(page);
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(ticketInfoDto);

        Page<TicketInfoDto> result = ticketInfoService.getByAttractionId(testId, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateTicketInfo_ShouldUpdateFields() {
        TicketInfoDto updateDto = new TicketInfoDto();
        updateDto.setPrice(BigDecimal.valueOf(200));
        updateDto.setCurrency("EUR");

        when(ticketInfoRepository.findById(testId)).thenReturn(Optional.of(ticketInfo));
        when(ticketInfoRepository.save(ticketInfo)).thenReturn(ticketInfo);
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(updateDto);

        TicketInfoDto result = ticketInfoService.updateTicketInfo(testId, updateDto);

        assertEquals(BigDecimal.valueOf(200), result.getPrice());
        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void getTicketInfoById_ShouldReturnTicketInfo() {
        when(ticketInfoRepository.findById(testId)).thenReturn(Optional.of(ticketInfo));
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(ticketInfoDto);

        TicketInfoDto result = ticketInfoService.getTicketInfoById(testId);

        assertEquals(ticketInfoDto, result);
    }

    @Test
    void deleteTicketInfo_ShouldSuccess() {
        when(ticketInfoRepository.existsById(testId)).thenReturn(true);

        ticketInfoService.deleteTicketInfo(testId);

        verify(ticketInfoRepository).deleteById(testId);
    }

    @Test
    void deleteTicketInfo_ShouldThrowWhenNotFound() {
        when(ticketInfoRepository.existsById(testId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> ticketInfoService.deleteTicketInfo(testId));
    }

    @Test
    void getByAttraction_ShouldReturnTicketInfo() {
        when(ticketInfoRepository.findByAttraction_Id(testId))
                .thenReturn(Optional.of(ticketInfo));
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(ticketInfoDto);

        TicketInfoDto result = ticketInfoService.getByAttraction(testId);

        assertEquals(ticketInfoDto, result);
    }
}

