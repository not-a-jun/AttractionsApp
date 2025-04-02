package com.AstonProgect.service;

import com.AstonProgect.dto.AddressDto;
import com.AstonProgect.mapper.AddressMapper;
import com.AstonProgect.model.Address;
import com.AstonProgect.repository.AddressRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для сервиса {@link AddressService}.
 * <p>
 * Класс проверяет функциональность сервиса адресов, используя моки
 * для репозитория и маппера, чтобы изолировать тестирование только логики сервиса.
 */
@ExtendWith(MockitoExtension.class)
class AddressServiceUnitTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressService addressService;

    private AddressDto addressDto;
    private Address address;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        addressDto = new AddressDto();
        addressDto.setId(testId);
        addressDto.setCity("Test City");
        addressDto.setStreet("Test Street");
        addressDto.setBuilding(1);
        addressDto.setRegion("Test Region");

        address = new Address();
        address.setId(testId);
        address.setCity("Test City");
    }

    @Test
    void createAddress_ShouldReturnCreatedAddress() {
        when(addressMapper.toEntity(addressDto)).thenReturn(address);
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.createAddress(addressDto);

        assertNotNull(result);
        assertEquals(addressDto.getCity(), result.getCity());
    }

    @Test
    void getAddressById_ShouldReturnAddress() {
        when(addressRepository.findById(testId)).thenReturn(Optional.of(address));
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.getAddressById(testId);

        assertEquals(addressDto, result);
    }

    @Test
    void updateAddress_ShouldUpdateFields() {
        when(addressRepository.findById(testId)).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        AddressDto result = addressService.updateAddress(testId, addressDto);

        assertNotNull(result);
        verify(addressMapper).updateAddressFromDto(addressDto, address);
    }

    @Test
    void searchAddresses_ShouldReturnFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> page = new PageImpl<>(List.of(address));

        when(addressRepository.searchAddresses(any(), any(), any(), eq(pageable)))
                .thenReturn(page);
        when(addressMapper.toDto(address)).thenReturn(addressDto);

        Page<AddressDto> result = addressService.searchAddresses(
                "city", "region", "street", pageable);

        assertEquals(1, result.getTotalElements());
    }
}
