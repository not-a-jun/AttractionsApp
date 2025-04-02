package com.AstonProgect.controller;

import com.AstonProgect.config.BaseControllerTest;
import com.AstonProgect.dto.AddressDto;
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

@WebMvcTest(AddressController.class)
public class AddressControllerTest extends BaseControllerTest {

    @MockBean
    private AddressService addressService;

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
    void createAddress_ShouldReturnCreated() throws Exception {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity("Test City");
        addressDto.setStreet("Test Street");
        addressDto.setRegion("Test Region");
        addressDto.setBuilding(1);
        addressDto.setLongitude(10.0);
        addressDto.setLatitude(20.0);

        when(addressService.createAddress(any(AddressDto.class)))
                .thenReturn(addressDto);

        mockMvc.perform(post("/api/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Test City"));
    }

    @Test
    void getAddressById_ShouldReturnAddress() throws Exception {
        UUID addressId = UUID.randomUUID();
        AddressDto addressDto = new AddressDto();
        addressDto.setId(addressId);
        addressDto.setCity("New York");

        when(addressService.getAddressById(addressId))
                .thenReturn(addressDto);

        mockMvc.perform(get("/api/addresses/{id}", addressId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.city").value("New York"));
    }


    @Test
    void getAllAddresses_ShouldReturnPage() throws Exception {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity("Test City");
        Page<AddressDto> page = new PageImpl<>(Collections.singletonList(addressDto));

        when(addressService.getAllAddresses(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/addresses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("Test City"));
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddress() throws Exception {
        UUID addressId = UUID.randomUUID();
        AddressDto requestDto = new AddressDto();
        requestDto.setCity("Updated City");
        requestDto.setStreet("Updated Street");
        requestDto.setBuilding(2);
        requestDto.setRegion("Updated Region");
        requestDto.setLongitude(10.0);
        requestDto.setLatitude(20.0);

        AddressDto responseDto = new AddressDto();
        responseDto.setId(addressId);
        responseDto.setCity("Updated City");
        responseDto.setStreet("Updated Street");
        responseDto.setBuilding(2);
        responseDto.setRegion("Updated Region");
        responseDto.setLongitude(10.0);
        responseDto.setLatitude(20.0);

        when(addressService.updateAddress(addressId, requestDto))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/addresses/{id}", addressId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(addressId.toString()))
                .andExpect(jsonPath("$.city").value("Updated City"))
                .andExpect(jsonPath("$.street").value("Updated Street"))
                .andExpect(jsonPath("$.building").value(2))
                .andExpect(jsonPath("$.region").value("Updated Region"));
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/addresses/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchAddresses_ShouldReturnFilteredResults() throws Exception {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity("Test City");
        Page<AddressDto> page = new PageImpl<>(Collections.singletonList(addressDto));

        when(addressService.searchAddresses(any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/addresses/search")
                        .param("city", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].city").value("Test City"));
    }
}
