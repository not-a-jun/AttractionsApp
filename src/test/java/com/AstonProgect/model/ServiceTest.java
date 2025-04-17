package com.AstonProgect.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceTest {

    private final Validator validator;

    public ServiceTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private Service createValidService() {
        Service service = new Service();
        service.setName("Guided Tour");
        service.setDescription("A tour with a professional guide");
        service.setServiceType(ServiceType.GUIDE);
        service.setPrice(new BigDecimal("50.00"));
        service.setCurrency("USD");
        return service;
    }

    @Test
    void shouldCreateValidService() {
        Service service = createValidService();

        assertThat(validator.validate(service)).isEmpty();
        assertThat(service.getName()).isEqualTo("Guided Tour");
        assertThat(service.getDescription()).isEqualTo("A tour with a professional guide");
        assertThat(service.getServiceType()).isEqualTo(ServiceType.GUIDE);
        assertThat(service.getPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(service.getCurrency()).isEqualTo("USD");
        assertThat(service.isAvailability()).isTrue();
    }

    @Test
    void shouldFailValidationWhenRequiredFieldsAreMissing() {
        Service service = new Service();

        Set<ConstraintViolation<Service>> violations = validator.validate(service);
        assertThat(violations).hasSize(3);
        assertThat(violations)
                .extracting("message")
                .contains(
                        "Name cannot be blank",
                        "Description cannot be blank",
                        "Service type cannot be null"
                );
    }

    @Test
    void shouldFailWhenCurrencyIsInvalid() {
        Service service = createValidService();
        service.setCurrency("US");

        Set<ConstraintViolation<Service>> violations = validator.validate(service);
        assertThat(violations)
                .extracting("message")
                .contains("Currency must be exactly 3 characters");
    }

    @Test
    void shouldUpdateAttractionRelation() {
        Service service = createValidService();
        Attraction attraction1 = new Attraction();
        Attraction attraction2 = new Attraction();

        service.setAttractions(Set.of(attraction1));
        service.setAttractions(Set.of(attraction2));

        assertThat(service.getAttractions())
                .hasSize(1)
                .containsExactly(attraction2);
    }

    @Test
    void shouldHandleNullAttractions() {
        Service service = createValidService();
        service.setAttractions(null);
        assertThat(service.getAttractions()).isNull();
    }

    @Test
    void shouldAcceptVariousPriceValues() {
        Service service = createValidService();

        service.setPrice(new BigDecimal("0.00"));
        assertThat(validator.validate(service)).isEmpty();

        service.setPrice(new BigDecimal("-10.00"));
        assertThat(validator.validate(service)).isEmpty();
    }

    @Test
    void shouldHaveDefaultAvailability() {
        Service service = new Service();
        assertThat(service.isAvailability()).isTrue();
    }

    @Test
    void shouldHandleNullPrice() {
        Service service = createValidService();
        service.setPrice(null);
        assertThat(validator.validate(service)).isEmpty();
    }

    @Test
    void shouldHandleNullCurrency() {
        Service service = createValidService();
        service.setCurrency(null);
        assertThat(validator.validate(service)).isEmpty();
    }

    @Test
    void shouldHandleNegativePrice() {
        Service service = createValidService();
        service.setPrice(new BigDecimal("-10.00"));

        assertThat(validator.validate(service)).isEmpty();
    }

    @Test
    void shouldHandleAllServiceTypes() {
        Service service = createValidService();

        for (ServiceType type : ServiceType.values()) {
            service.setServiceType(type);
            assertThat(validator.validate(service)).isEmpty();
        }
    }
}