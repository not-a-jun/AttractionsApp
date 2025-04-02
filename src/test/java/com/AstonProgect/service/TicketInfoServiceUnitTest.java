package com.AstonProgect.service;

import com.AstonProgect.dto.TicketInfoDto;
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
    void getByAttractionId_ShouldReturnTicketInfo() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TicketInfo> page = new PageImpl<>(List.of(ticketInfo));

        when(attractionRepository.existsById(testId)).thenReturn(true);
        when(ticketInfoRepository.findByAttraction_Id(testId, pageable)).thenReturn(page);
        when(ticketInfoMapper.toDto(ticketInfo)).thenReturn(ticketInfoDto);

        Page<TicketInfoDto> result = ticketInfoService.getByAttractionId(testId, pageable);

        assertEquals(1, result.getTotalElements());
    }
}

