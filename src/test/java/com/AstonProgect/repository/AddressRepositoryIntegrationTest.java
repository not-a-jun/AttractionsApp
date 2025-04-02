package com.AstonProgect.repository;

import com.AstonProgect.AstonProjectApplication;
import com.AstonProgect.config.TestcontainersConfig;
import com.AstonProgect.model.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AstonProjectApplication.class, TestcontainersConfig.class})
@ActiveProfiles("integration-test")
@Transactional
@Import(TestcontainersConfig.class)
public class AddressRepositoryIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void shouldSaveAndRetrieveAddress() {
        // Arrange
        Address address = new Address();
        address.setCity("Test City");
        address.setStreet("Test Street");
        address.setBuilding(1);
        address.setRegion("Test Region");

        // Act
        Address saved = addressRepository.save(address);
        Optional<Address> found = addressRepository.findById(saved.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test City", found.get().getCity());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void shouldFindByCityWithPagination() {
        // Arrange
        Address address1 = new Address();
        address1.setCity("Moscow");
        address1.setStreet("Tverskaya");
        address1.setBuilding(1);
        address1.setRegion("Moscow Oblast");
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setCity("Moscow");
        address2.setStreet("Arbat");
        address2.setBuilding(2);
        address2.setRegion("Moscow Oblast");
        addressRepository.save(address2);

        Address address3 = new Address();
        address3.setCity("Saint Petersburg");
        address3.setStreet("Nevsky Prospect");
        address3.setBuilding(3);
        address3.setRegion("Leningrad Oblast");
        addressRepository.save(address3);

        // Act
        Page<Address> result = addressRepository.findByCity(
                "Moscow", PageRequest.of(0, 10));

        // Assert
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .allMatch(a -> a.getCity().equals("Moscow")));
    }

    @Test
    void shouldSearchAddresses() {
        // Arrange
        Address address = new Address();
        address.setCity("New York");
        address.setRegion("NY");
        address.setStreet("Broadway");
        address.setBuilding(42);
        addressRepository.save(address);

        // Act
        Page<Address> result = addressRepository.searchAddresses(
                "New", "NY", "Broad", PageRequest.of(0, 10));

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("New York", result.getContent().get(0).getCity());
    }
}
