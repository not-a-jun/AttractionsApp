package com.AstonProgect.service;

import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.exception.ResourceNotFoundException;
import com.AstonProgect.mapper.AttractionMapper;
import com.AstonProgect.model.Attraction;
import com.AstonProgect.model.AttractionType;
import com.AstonProgect.model.Service;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.AttractionRepository;
import com.AstonProgect.repository.ServiceRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttractionServiceUnitTest {

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private TicketInfoRepository ticketInfoRepository;

    @Mock
    private AttractionMapper attractionMapper;

    @InjectMocks
    private AttractionService attractionService;

    private AttractionDto attractionDto;
    private Attraction attraction;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        attractionDto = new AttractionDto();
        attractionDto.setId(testId);
        attractionDto.setName("Test Attraction");
        attractionDto.setDescription("Test Description");
        attractionDto.setAttractionType(AttractionType.MUSEUM);
        attractionDto.setAddressId(UUID.randomUUID());
        attractionDto.setServiceIds(Set.of(UUID.randomUUID()));
        attractionDto.setTicketInfoId(UUID.randomUUID());

        attraction = new Attraction();
        attraction.setId(testId);
        attraction.setName("Test Attraction");
    }
    
    @Test
    void createAttraction_ShouldSuccess() {
        when(addressRepository.existsById(attractionDto.getAddressId())).thenReturn(true);
        when(ticketInfoRepository.existsById(attractionDto.getTicketInfoId())).thenReturn(true);
        when(serviceRepository.existsById(any())).thenReturn(true);
        when(attractionMapper.toEntity(attractionDto)).thenReturn(attraction);
        when(attractionRepository.save(attraction)).thenReturn(attraction);
        when(attractionMapper.toDto(attraction)).thenReturn(attractionDto);

        AttractionDto result = attractionService.createAttraction(attractionDto);

        assertNotNull(result);
        assertEquals(attractionDto.getName(), result.getName());
        verify(attractionRepository).save(attraction);
    }

    @Test
    void createAttraction_ShouldThrowWhenAddressNotFound() {
        when(addressRepository.existsById(attractionDto.getAddressId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> attractionService.createAttraction(attractionDto));
    }

    @Test
    void createAttraction_ShouldThrowWhenServiceNotFound() {
        when(addressRepository.existsById(any())).thenReturn(true);
        when(ticketInfoRepository.existsById(any())).thenReturn(true);
        when(serviceRepository.existsById(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> attractionService.createAttraction(attractionDto));
    }

    // READ TESTS
    @Test
    void getAttractionById_ShouldReturnAttraction() {
        when(attractionRepository.findById(testId)).thenReturn(Optional.of(attraction));
        when(attractionMapper.toDto(attraction)).thenReturn(attractionDto);

        AttractionDto result = attractionService.getAttractionById(testId);

        assertEquals(attractionDto, result);
    }

    @Test
    void getAttractionsByServiceId_ShouldReturnFiltered() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attraction> page = new PageImpl<>(List.of(attraction));

        when(serviceRepository.existsById(testId)).thenReturn(true);
        when(attractionRepository.findByServiceId(testId, pageable)).thenReturn(page);
        when(attractionMapper.toDto(attraction)).thenReturn(attractionDto);

        Page<AttractionDto> result = attractionService.getAttractionsByServiceId(testId, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAttractionById_ShouldThrowWhenNotFound() {
        when(attractionRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> attractionService.getAttractionById(testId));
    }

    @Test
    void getAllAttractions_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attraction> page = new PageImpl<>(List.of(attraction));

        when(attractionRepository.findAll(pageable)).thenReturn(page);
        when(attractionMapper.toDto(attraction)).thenReturn(attractionDto);

        Page<AttractionDto> result = attractionService.getAllAttractions(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(attractionDto, result.getContent().get(0));
    }

    // UPDATE TESTS
    @Test
    void updateAttraction_ShouldUpdateFields() {
        AttractionDto updateDto = new AttractionDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Desc");
        updateDto.setAttractionType(AttractionType.PARK);

        when(attractionRepository.findById(testId)).thenReturn(Optional.of(attraction));
        when(attractionRepository.save(attraction)).thenReturn(attraction);
        when(attractionMapper.toDto(attraction)).thenReturn(updateDto);

        AttractionDto result = attractionService.updateAttraction(testId, updateDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Desc", result.getDescription());
        assertEquals(AttractionType.PARK, result.getAttractionType());
    }

    @Test
    void updateAttraction_ShouldUpdateServices() {
        UUID newServiceId = UUID.randomUUID();
        Service newService = new Service();
        newService.setId(newServiceId);

        AttractionDto updateDto = new AttractionDto();
        updateDto.setServiceIds(Set.of(newServiceId));

        when(serviceRepository.findById(newServiceId)).thenReturn(Optional.of(newService));
        when(attractionRepository.findById(testId)).thenReturn(Optional.of(attraction));

        attractionService.updateAttraction(testId, updateDto);

        assertEquals(1, attraction.getServices().size());
        assertTrue(attraction.getServices().contains(newService));
    }

    @Test
    void updateAttraction_ShouldThrowWhenNotFound() {
        when(attractionRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> attractionService.updateAttraction(testId, attractionDto));
    }

    // DELETE TESTS
    @Test
    void deleteAttraction_ShouldSuccess() {
        when(attractionRepository.existsById(testId)).thenReturn(true);

        attractionService.deleteAttraction(testId);

        verify(attractionRepository).deleteById(testId);
    }

    @Test
    void deleteAttraction_ShouldThrowWhenNotFound() {
        when(attractionRepository.existsById(testId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> attractionService.deleteAttraction(testId));
    }

    // SEARCH TESTS
    @Test
    void searchAttractions_ShouldReturnFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Attraction> page = new PageImpl<>(List.of(attraction));

        when(attractionRepository.searchAttractions(any(), any(), any(), eq(pageable)))
                .thenReturn(page);
        when(attractionMapper.toDto(attraction)).thenReturn(attractionDto);

        Page<AttractionDto> result = attractionService.searchAttractions(
                "test", AttractionType.MUSEUM, "city", pageable);

        assertEquals(1, result.getTotalElements());
    }
}
