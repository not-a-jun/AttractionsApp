package com.AstonProgect.controller;

import com.AstonProgect.config.BaseControllerTest;
import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.model.AttractionType;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import com.AstonProgect.service.AddressService;
import com.AstonProgect.service.AttractionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttractionController.class)
public class AttractionControllerTest extends BaseControllerTest {

    @MockBean
    private AttractionService attractionService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private AttractionRepository attractionRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private TicketInfoRepository ticketInfoRepository;

    @Test
    void createAttraction_ShouldReturnCreated() throws Exception {
        AttractionDto attractionDto = new AttractionDto();
        attractionDto.setName("Test Attraction");
        attractionDto.setDescription("Test Description");
        attractionDto.setAttractionType(AttractionType.MUSEUM);
        attractionDto.setAddressId(UUID.randomUUID());

        when(attractionService.createAttraction(any(AttractionDto.class)))
                .thenReturn(attractionDto);

        when(addressRepository.existsById(any(UUID.class)))
                .thenReturn(true);

        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(attractionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Attraction"));
    }

    @Test
    void getAttractionById_ShouldReturnAttraction() throws Exception {
        UUID attractionId = UUID.randomUUID();
        AttractionDto attractionDto = new AttractionDto();
        attractionDto.setId(attractionId);
        attractionDto.setName("Test Attraction");

        when(attractionService.getAttractionById(attractionId))
                .thenReturn(attractionDto);

        mockMvc.perform(get("/api/attractions/{id}", attractionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attractionId.toString()))
                .andExpect(jsonPath("$.name").value("Test Attraction"));
    }

    @Test
    void searchAttractions_ShouldReturnFilteredResults() throws Exception {
        AttractionDto attractionDto = new AttractionDto();
        attractionDto.setName("Test Attraction");
        Page<AttractionDto> page = new PageImpl<>(Collections.singletonList(attractionDto));

        when(attractionService.searchAttractions(any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/attractions/search")
                        .param("name", "Test")
                        .param("type", "MUSEUM")
                        .param("city", "City")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Attraction"));
    }
}
