package com.AstonProgect.controller;

import com.AstonProgect.config.BaseControllerTest;
import com.AstonProgect.dto.TicketInfoDto;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import com.AstonProgect.service.AttractionService;
import com.AstonProgect.service.TicketInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketInfoController.class)
class TicketInfoControllerTest extends BaseControllerTest {

    @MockBean
    private TicketInfoService ticketInfoService;

    @MockBean
    private AttractionService attractionService;

    @MockBean
    private AttractionRepository attractionRepository;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private TicketInfoRepository ticketInfoRepository;


    @Test
    void createTicketInfo_ShouldReturnCreated() throws Exception {

        TicketInfoDto requestDto = ticketInfoDto();
        TicketInfoDto responseDto = ticketInfoDto();


        when(attractionRepository.existsById(requestDto.getAttractionId()))
                .thenReturn(true);
        when(ticketInfoRepository.existsByAttraction_Id(requestDto.getAttractionId()))
                .thenReturn(false);
        when(ticketInfoService.createTicketInfo(any(TicketInfoDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.availability").value(true));
    }

    @Test
    void getByAttractionId_ShouldReturnTicketInfos() throws Exception {
        UUID attractionId = UUID.randomUUID();
        TicketInfoDto ticketInfoDto = ticketInfoDto();
        Page<TicketInfoDto> page = new PageImpl<>(Collections.singletonList(ticketInfoDto));

        when(attractionRepository.existsById(attractionId))
                .thenReturn(true);
        when(ticketInfoService.getByAttractionId(any(UUID.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/tickets/attraction/{attractionId}", attractionId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price").value(100))
                .andExpect(jsonPath("$.content[0].currency").value("USD"))
                .andExpect(jsonPath("$.content[0].availability").value(true));
    }

    private TicketInfoDto ticketInfoDto() {
        TicketInfoDto dto = new TicketInfoDto();
        dto.setId(UUID.randomUUID());
        dto.setPrice(BigDecimal.valueOf(100));
        dto.setCurrency("USD");
        dto.setAvailability(true);
        dto.setAttractionId(UUID.randomUUID());
        return dto;
    }
}
