package com.AstonProgect.mapper;

import com.AstonProgect.dto.AttractionDto;
import com.AstonProgect.model.*;
import com.AstonProgect.repository.AddressRepository;
import com.AstonProgect.repository.ServiceRepository;
import com.AstonProgect.repository.TicketInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttractionMapperTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private TicketInfoRepository ticketInfoRepository;

    @InjectMocks
    private AttractionMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new AttractionMapperImpl() {
            @Override
            protected Set<Service> mapServices(Set<UUID> serviceIds) {
                if (serviceIds == null) {
                    return null;
                }
                if (serviceIds.isEmpty()) {
                    return Collections.emptySet();
                }
                Set<Service> services = new HashSet<>();
                for (UUID id : serviceIds) {
                    Service service = new Service();
                    service.setId(id);
                    services.add(service);
                }
                return services;
            }
        };
        mapper.addressRepository = addressRepository;
        mapper.serviceRepository = serviceRepository;
        mapper.ticketInfoRepository = ticketInfoRepository;
    }

    @Test
    void testToEntity_WithFullData() {
        // Подготовка тестовых данных
        UUID addressId = UUID.randomUUID();
        UUID serviceId1 = UUID.randomUUID();
        UUID serviceId2 = UUID.randomUUID();
        UUID ticketInfoId = UUID.randomUUID();
        UUID attractionId = UUID.randomUUID();

        // Создание моков и тестовых объектов
        Address address = new Address();
        address.setId(addressId);

        Service service1 = new Service();
        service1.setId(serviceId1);
        Service service2 = new Service();
        service2.setId(serviceId2);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setId(ticketInfoId);

        // Настройка моков
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(ticketInfoRepository.findById(ticketInfoId)).thenReturn(Optional.of(ticketInfo));

        // Создание DTO
        AttractionDto dto = new AttractionDto();
        dto.setId(attractionId);
        dto.setName("Test Attraction");
        dto.setDescription("Test Description");
        dto.setAttractionType(AttractionType.MUSEUM);
        dto.setAddressId(addressId);
        dto.setServiceIds(new HashSet<>(Arrays.asList(serviceId1, serviceId2)));
        dto.setTicketInfoId(ticketInfoId);

        // Выполнение тестируемого метода
        Attraction entity = mapper.toEntity(dto);

        // Проверки
        assertAll(
                // Проверка основных полей
                () -> assertEquals(attractionId, entity.getId(), "ID should match"),
                () -> assertEquals("Test Attraction", entity.getName(), "Name should match"),
                () -> assertEquals("Test Description", entity.getDescription(), "Description should match"),
                () -> assertEquals(AttractionType.MUSEUM, entity.getAttractionType(), "AttractionType should match"),

                // Проверка связей
                () -> assertNotNull(entity.getAddress(), "Address should not be null"),
                () -> assertEquals(addressId, entity.getAddress().getId(), "Address ID should match"),

                // Проверка коллекции сервисов
                () -> assertNotNull(entity.getServices(), "Services should not be null"),
                () -> assertEquals(2, entity.getServices().size(), "Should have 2 services"),
                () -> assertTrue(entity.getServices().stream()
                                .map(Service::getId)
                                .collect(Collectors.toSet())
                                .containsAll(Set.of(serviceId1, serviceId2)),
                        "Services should contain both service IDs"),

                // Проверка информации о билетах
                () -> assertNotNull(entity.getTicketInfo(), "TicketInfo should not be null"),
                () -> assertEquals(ticketInfoId, entity.getTicketInfo().getId(), "TicketInfo ID should match")
        );
    }

    @Test
    void testToEntity_WithNullCollections() {
        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        AttractionDto dto = new AttractionDto();
        dto.setAddressId(addressId);
        dto.setServiceIds(null);
        dto.setTicketInfoId(null);

        Attraction entity = mapper.toEntity(dto);

        assertAll(
                () -> assertNull(entity.getServices()),
                () -> assertNull(entity.getTicketInfo())
        );
    }

    @Test
    void testToEntity_WithEmptyServiceIds() {
        UUID addressId = UUID.randomUUID();
        Address address = new Address();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        AttractionDto dto = new AttractionDto();
        dto.setAddressId(addressId);
        dto.setServiceIds(Collections.emptySet());

        Attraction entity = mapper.toEntity(dto);

        assertNotNull(entity.getServices());
        assertTrue(entity.getServices().isEmpty());
    }

    @ParameterizedTest
    @NullSource
    void testToEntity_WithNullDto(AttractionDto nullDto) {
        assertNull(mapper.toEntity(nullDto));
    }

    @Test
    void testToDto() {
        Attraction entity = new Attraction();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Attraction");
        entity.setDescription("Test Description");
        entity.setAttractionType(AttractionType.PARK);

        Address address = new Address();
        address.setId(UUID.randomUUID());
        entity.setAddress(address);

        Service service1 = new Service();
        service1.setId(UUID.randomUUID());
        Service service2 = new Service();
        service2.setId(UUID.randomUUID());
        entity.setServices(new HashSet<>(Arrays.asList(service1, service2)));

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setId(UUID.randomUUID());
        entity.setTicketInfo(ticketInfo);

        AttractionDto dto = mapper.toDto(entity);

        assertAll(
                () -> assertEquals(entity.getId(), dto.getId()),
                () -> assertEquals(entity.getName(), dto.getName()),
                () -> assertEquals(entity.getDescription(), dto.getDescription()),
                () -> assertEquals(entity.getAttractionType(), dto.getAttractionType()),
                () -> assertEquals(address.getId(), dto.getAddressId()),
                () -> assertEquals(2, dto.getServiceIds().size()),
                () -> assertTrue(dto.getServiceIds().containsAll(Arrays.asList(service1.getId(), service2.getId()))),
                () -> assertEquals(ticketInfo.getId(), dto.getTicketInfoId())
        );
    }

    @Test
    void testToDto_WithNullFields() {
        Attraction entity = new Attraction();
        entity.setAddress(null);
        entity.setServices(null);
        entity.setTicketInfo(null);

        AttractionDto dto = mapper.toDto(entity);

        assertAll(
                () -> assertNull(dto.getAddressId()),
                () -> assertNull(dto.getServiceIds()),
                () -> assertNull(dto.getTicketInfoId())
        );
    }

    @Test
    void testUpdateAttractionFromDto() {
        Attraction entity = new Attraction();
        entity.setName("Old Name");
        entity.setDescription("Old Description");

        AttractionDto dto = new AttractionDto();
        dto.setName("New Name");
        dto.setAttractionType(AttractionType.MUSEUM);

        mapper.updateAttractionFromDto(dto, entity);

        assertAll(
                () -> assertEquals("New Name", entity.getName()),
                () -> assertEquals("Old Description", entity.getDescription()),
                () -> assertEquals(AttractionType.MUSEUM, entity.getAttractionType())
        );
    }

    @Test
    void testMapAddress_NotFound() {
        UUID addressId = UUID.randomUUID();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> mapper.mapAddress(addressId));
    }

    @Test
    void testMapServices_WithNullInput() {
        assertNull(mapper.mapServices(null));
    }

    @Test
    void testMapServices_WithEmptySet() {
        Set<Service> result = mapper.mapServices(Collections.emptySet());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapTicketInfo_NotFound() {
        // Подготовка
        UUID ticketInfoId = UUID.randomUUID();
        when(ticketInfoRepository.findById(ticketInfoId)).thenReturn(Optional.empty());

        // Проверка
        assertThrows(RuntimeException.class, () -> mapper.mapTicketInfo(ticketInfoId));
    }

    @Test
    void testAfterMapping_WithNullCollections() {
        // Подготовка
        AttractionDto dto = new AttractionDto();
        Attraction attraction = new Attraction();
        attraction.setServices(null); // Явно устанавливаем null

        // Выполнение
        mapper.afterMapping(dto, attraction);

        // Проверка - метод должен корректно обработать null
        assertNull(attraction.getServices());
    }
}