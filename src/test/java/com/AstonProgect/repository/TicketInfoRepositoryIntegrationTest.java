package com.AstonProgect.repository;

import com.AstonProgect.AstonProjectApplication;
import com.AstonProgect.config.TestcontainersConfig;
import com.AstonProgect.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("integration-test")
@SpringBootTest(classes = {AstonProjectApplication.class, TestcontainersConfig.class})
@Transactional
@Import(TestcontainersConfig.class)
public class TicketInfoRepositoryIntegrationTest {

    @Autowired
    private TicketInfoRepository ticketInfoRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void shouldSaveAndFindTicketInfo() {
        // Arrange
        Address address = createAddress("Paris", "Champs-Élysées", 1, "Île-de-France");
        Attraction attraction = createAttraction("Eiffel Tower", "Iconic tower", AttractionType.PARK, address);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setPrice(BigDecimal.valueOf(25.99));
        ticketInfo.setCurrency("EUR");
        ticketInfo.setAvailability(true);
        ticketInfo.setAttraction(attraction);

        // Act
        TicketInfo saved = ticketInfoRepository.save(ticketInfo);
        Optional<TicketInfo> found = ticketInfoRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(0, BigDecimal.valueOf(25.99).compareTo(found.get().getPrice()));
        assertEquals("EUR", found.get().getCurrency());
    }

    @Test
    void shouldFindByAttractionId() {
        // Arrange
        Address address = createAddress("New York", "5th Avenue", 1, "NY");
        Attraction attraction = createAttraction("Statue of Liberty", "Iconic statue", AttractionType.PARK, address);

        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setPrice(BigDecimal.valueOf(18.50));
        ticketInfo.setCurrency("USD");
        ticketInfo.setAvailability(true);
        ticketInfo.setAttraction(attraction);
        ticketInfoRepository.save(ticketInfo);

        // Act
        Page<TicketInfo> result = ticketInfoRepository.findByAttraction_Id(
                attraction.getId(),
                PageRequest.of(0, 10)
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("USD", result.getContent().get(0).getCurrency());
    }

    @Test
    void shouldSearchTicketInfos() {
        // Arrange
        Address address1 = createAddress("Rome", "Piazza del Colosseo", 1, "Lazio");
        Attraction colosseum = createAttraction("Colosseum", "Ancient amphitheater", AttractionType.ARCHAEOLOGICAL_SITE, address1);

        TicketInfo ticket1 = new TicketInfo();
        ticket1.setPrice(BigDecimal.valueOf(16.00));
        ticket1.setCurrency("EUR");
        ticket1.setAvailability(true);
        ticket1.setAttraction(colosseum);
        ticketInfoRepository.save(ticket1);

        Address address2 = createAddress("London", "Westminster", 1, "England");
        Attraction londonEye = createAttraction("London Eye", "Ferris wheel", AttractionType.PARK, address2);

        TicketInfo ticket2 = new TicketInfo();
        ticket2.setPrice(BigDecimal.valueOf(32.50));
        ticket2.setCurrency("GBP");
        ticket2.setAvailability(false);
        ticket2.setAttraction(londonEye);
        ticketInfoRepository.save(ticket2);

        // Act
        Page<TicketInfo> eurTickets = ticketInfoRepository.searchTicketInfos(
                "EUR",
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        Page<TicketInfo> availableTickets = ticketInfoRepository.searchTicketInfos(
                null,
                true,
                null,
                null,
                PageRequest.of(0, 10)
        );

        // Assert
        assertEquals(1, eurTickets.getTotalElements());
        assertEquals(1, availableTickets.getTotalElements());
        assertEquals("Colosseum", eurTickets.getContent().get(0).getAttraction().getName());
    }

    private Address createAddress(String city, String street, int building, String region) {
        Address address = new Address();
        address.setCity(city);
        address.setStreet(street);
        address.setBuilding(building);
        address.setRegion(region);
        return addressRepository.save(address);
    }

    private Attraction createAttraction(String name, String description, AttractionType type, Address address) {
        Attraction attraction = new Attraction();
        attraction.setName(name);
        attraction.setDescription(description);
        attraction.setAttractionType(type);
        attraction.setAddress(address);
        return attractionRepository.save(attraction);
    }
}