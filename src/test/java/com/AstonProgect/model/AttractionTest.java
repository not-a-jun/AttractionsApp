package com.AstonProgect.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AttractionTest {

    private final Validator validator;

    public AttractionTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private Address createValidAddress() {
        Address address = new Address();
        address.setBuilding(10);
        address.setStreet("Main Street");
        address.setCity("New York");
        address.setRegion("NY");
        address.setLongitude(40.7128);
        address.setLatitude(-74.0060);
        return address;
    }

    private Attraction createValidAttraction() {
        Attraction attraction = new Attraction();
        attraction.setName("Colosseum");
        attraction.setDescription("Ancient amphitheater in Rome");
        attraction.setAttractionType(AttractionType.ARCHAEOLOGICAL_SITE);
        attraction.setAddress(createValidAddress());
        return attraction;
    }

    @Test
    void shouldCreateValidAttraction() {
        Attraction attraction = createValidAttraction();

        assertThat(validator.validate(attraction)).isEmpty();
        assertThat(attraction.getName()).isEqualTo("Colosseum");
        assertThat(attraction.getDescription()).isEqualTo("Ancient amphitheater in Rome");
        assertThat(attraction.getAttractionType()).isEqualTo(AttractionType.ARCHAEOLOGICAL_SITE);
        assertThat(attraction.getAddress().getCity()).isEqualTo("New York");
    }

    @Test
    void shouldFailValidationWhenRequiredFieldsAreMissing() {
        Attraction attraction = new Attraction();

        Set<ConstraintViolation<Attraction>> violations = validator.validate(attraction);
        assertThat(violations).hasSize(4);
        assertThat(violations)
                .extracting("message")
                .contains(
                        "Name cannot be blank",
                        "Description cannot be blank",
                        "Attraction type cannot be null",
                        "Address cannot be null"
                );
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        Attraction attraction = createValidAttraction();
        attraction.setDescription("a".repeat(1001));

        Set<ConstraintViolation<Attraction>> violations = validator.validate(attraction);
        assertThat(violations)
                .extracting("message")
                .contains("Description must be less than 1000 characters");
    }

    @Test
    void shouldHandleNullTicketInfo() {
        Attraction attraction = createValidAttraction();
        attraction.setTicketInfo(null);
        assertThat(attraction.getTicketInfo()).isNull();
    }

    @Test
    void shouldPreventDuplicateServices() {
        Attraction attraction = createValidAttraction();
        Service service = new Service();
        service.setName("Audio Guide");

        Set<Service> services = new HashSet<>();
        services.add(service);
        services.add(service);

        attraction.setServices(services);
        assertThat(attraction.getServices()).hasSize(1);
    }

    @Test
    void shouldFailWhenAddressIsNull() {
        Attraction attraction = createValidAttraction();
        attraction.setAddress(null);

        Set<ConstraintViolation<Attraction>> violations = validator.validate(attraction);
        assertThat(violations)
                .extracting("message")
                .contains("Address cannot be null");
    }

    @Test
    void shouldHandleEmptyServicesSet() {
        Attraction attraction = createValidAttraction();
        attraction.setServices(new HashSet<>());
        assertThat(attraction.getServices()).isEmpty();
    }

    @Test
    void shouldHandleNullServices() {
        Attraction attraction = createValidAttraction();
        attraction.setServices(null);
        assertThat(attraction.getServices()).isNull();
    }

    @Test
    void shouldManageTicketInfoRelationship() {
        Attraction attraction = createValidAttraction();
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setPrice(new BigDecimal("10.00"));

        attraction.setTicketInfo(ticketInfo);
        ticketInfo.setAttraction(attraction);

        assertThat(attraction.getTicketInfo()).isEqualTo(ticketInfo);
        assertThat(ticketInfo.getAttraction()).isEqualTo(attraction);
    }

    @Test
    void shouldValidateAttractionTypeRelations() {
        Attraction attraction = createValidAttraction();

        for (AttractionType type : AttractionType.values()) {
            attraction.setAttractionType(type);
            assertThat(validator.validate(attraction)).isEmpty();
        }
    }

    @Test
    void shouldHandleServiceRelations() {
        Attraction attraction = createValidAttraction();

        // Создаем два разных сервиса с уникальными именами
        Service service1 = new Service();
        service1.setName("Audio Guide");
        service1.setDescription("Audio tour guide");
        service1.setServiceType(ServiceType.GUIDE);

        Service service2 = new Service();
        service2.setName("VIP Tour");
        service2.setDescription("Exclusive VIP tour");
        service2.setServiceType(ServiceType.CAR_TOUR);

        attraction.setServices(Set.of(service1, service2));
        assertThat(attraction.getServices()).hasSize(2);
    }

    @Test
    void shouldHandleMaxDescriptionLength() {
        Attraction attraction = createValidAttraction();
        attraction.setDescription("a".repeat(1000));

        assertThat(validator.validate(attraction)).isEmpty();
    }

    @Test
    void shouldHandleAllAttractionTypes() {
        Attraction attraction = createValidAttraction();

        for (AttractionType type : AttractionType.values()) {
            attraction.setAttractionType(type);
            assertThat(validator.validate(attraction)).isEmpty();
        }
    }

    @Test
    void testBidirectionalRelationships() {
        Attraction attraction = new Attraction();
        attraction.setName("Test Attraction");
        attraction.setDescription("Test Description");
        attraction.setAttractionType(AttractionType.MUSEUM);

        Address address = new Address();
        address.setBuilding(1);
        address.setStreet("Test Street");
        address.setCity("Test City");
        address.setRegion("Test Region");

        // Устанавливаем двустороннюю связь
        attraction.setAddress(address);
        address.setAttractions(new ArrayList<>());
        address.getAttractions().add(attraction);

        assertThat(attraction.getAddress()).isEqualTo(address);
        assertThat(address.getAttractions()).containsExactly(attraction);
    }

    @Test
    void testServiceRelations() {
        Attraction attraction = new Attraction();
        attraction.setName("Test Attraction");
        attraction.setDescription("Test Description");
        attraction.setAttractionType(AttractionType.MUSEUM);

        Service service = new Service();
        service.setName("Test Service");
        service.setDescription("Test Service Description");
        service.setServiceType(ServiceType.GUIDE);

        // Устанавливаем двустороннюю связь
        attraction.setServices(new HashSet<>());
        attraction.getServices().add(service);
        service.setAttractions(new HashSet<>());
        service.getAttractions().add(attraction);

        assertThat(attraction.getServices()).containsExactly(service);
        assertThat(service.getAttractions()).containsExactly(attraction);
    }
}