package com.AstonProgect.controller;

import com.AstonProgect.config.BaseControllerTest;
import com.AstonProgect.dto.ServiceDto;
import com.AstonProgect.model.ServiceType;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import com.AstonProgect.service.AttractionService;
import com.AstonProgect.service.ServiceService;
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

@WebMvcTest(ServiceController.class)
class ServiceControllerTest extends BaseControllerTest {

    @MockBean
    private ServiceService serviceService;

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
    void createService_ShouldReturnCreated() throws Exception {
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setName("Test Service");
        serviceDto.setDescription("Test Description");
        serviceDto.setServiceType(ServiceType.GUIDE);

        ServiceDto responseDto = new ServiceDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setName(serviceDto.getName());

        when(serviceService.createService(any(ServiceDto.class)))
                .thenReturn(serviceDto);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(serviceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Service"));
    }

    @Test
    void getAllServiceTypes_ShouldReturnTypes() throws Exception {
        ServiceType[] expectedTypes = ServiceType.values();

        when(serviceService.getAllServiceTypes()).thenReturn(expectedTypes);

        mockMvc.perform(get("/api/services/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(expectedTypes.length));
    }

    @Test
    void searchServices_ShouldReturnFilteredResults() throws Exception {
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setName("Test Service");
        Page<ServiceDto> page = new PageImpl<>(Collections.singletonList(serviceDto));

        when(serviceService.searchServices(any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/services/search")
                        .param("name", "Test")
                        .param("type", "GUIDE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Service"));
    }
}