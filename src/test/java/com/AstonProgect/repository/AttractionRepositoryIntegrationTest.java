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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AstonProjectApplication.class, TestcontainersConfig.class})
@ActiveProfiles("integration-test")
@Transactional
@Import(TestcontainersConfig.class)
public class AttractionRepositoryIntegrationTest {

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void shouldSaveAttractionWithAddress() {
        // Arrange
        Address address = new Address();
        address.setCity("Paris");
        address.setStreet("Champs-Élysées");
        address.setBuilding(1);
        address.setRegion("Île-de-France");
        address = addressRepository.save(address);

        Attraction attraction = new Attraction();
        attraction.setName("Eiffel Tower");
        attraction.setDescription("Iconic iron tower");
        attraction.setAttractionType(AttractionType.ARCHAEOLOGICAL_SITE);
        attraction.setAddress(address);

        // Act
        Attraction saved = attractionRepository.save(attraction);
        Attraction found = attractionRepository.findWithAddressById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Attraction not found"));

        // Assert
        assertEquals("Eiffel Tower", found.getName());
        assertEquals("Paris", found.getAddress().getCity());
        assertEquals("Iconic iron tower", found.getDescription());
    }

    @Test
    void shouldFindByAttractionType() {
        // Arrange
        Address address = new Address();
        address.setCity("Test City");
        address.setStreet("Test Street");
        address.setBuilding(1);
        address.setRegion("Test Region");
        address = addressRepository.save(address);

        Attraction museum = new Attraction();
        museum.setName("Louvre");
        museum.setDescription("World's largest art museum");
        museum.setAttractionType(AttractionType.MUSEUM);
        museum.setAddress(address);
        attractionRepository.save(museum);

        Attraction park = new Attraction();
        park.setName("Central Park");
        park.setDescription("Urban park in Manhattan");
        park.setAttractionType(AttractionType.PARK);
        park.setAddress(address);
        attractionRepository.save(park);

        // Act
        Page<Attraction> result = attractionRepository.findByAttractionType(
                AttractionType.MUSEUM, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Louvre", result.getContent().get(0).getName());
    }

    @Test
    void shouldSearchAttractions() {
        // Arrange
        Address address = new Address();
        address.setCity("Rome");
        address.setStreet("Piazza del Colosseo");
        address.setBuilding(1);
        address.setRegion("Lazio");
        address = addressRepository.save(address);

        Attraction attraction = new Attraction();
        attraction.setName("Colosseum");
        attraction.setDescription("Ancient amphitheater");
        attraction.setAttractionType(AttractionType.ARCHAEOLOGICAL_SITE);
        attraction.setAddress(address);
        attractionRepository.save(attraction);

        // Act
        Page<Attraction> result = attractionRepository.searchAttractions(
                "Colos", AttractionType.ARCHAEOLOGICAL_SITE, "Rome",
                PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Colosseum", result.getContent().get(0).getName());
    }

    @Test
    void shouldFindByServiceId() {
        // Arrange
        Address address = new Address();
        address.setCity("London");
        address.setStreet("Westminster");
        address.setBuilding(1);
        address.setRegion("England");
        address = addressRepository.save(address);

        Service guide = new Service();
        guide.setName("Guide Service");
        guide.setDescription("Professional tour guide");
        guide.setServiceType(ServiceType.GUIDE);
        guide = serviceRepository.save(guide);

        Attraction attraction = new Attraction();
        attraction.setName("Big Ben");
        attraction.setDescription("Iconic clock tower");
        attraction.setAttractionType(AttractionType.PALACE);
        attraction.setAddress(address);
        attraction.setServices(Set.of(guide));
        attractionRepository.save(attraction);

        // Act
        Page<Attraction> result = attractionRepository.findByServiceId(
                guide.getId(), PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Big Ben", result.getContent().get(0).getName());
    }
}
