package com.AstonProgect.dto;

import com.AstonProgect.model.ServiceType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateAllConstraints() {
        ServiceDto dto = createValidServiceDto();
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailWhenNameConstraintsViolated() {
        ServiceDto dto = createValidServiceDto();
        dto.setName(null);
        assertValidationError(dto, "Name cannot be blank");

        dto = createValidServiceDto();
        dto.setName(" ");
        assertValidationError(dto, "Name cannot be blank");

        dto = createValidServiceDto();
        dto.setName("N".repeat(51));
        assertValidationError(dto, "Name must be less than 50 characters");
    }

    @Test
    void shouldFailWhenDescriptionConstraintsViolated() {
        ServiceDto dto = createValidServiceDto();
        dto.setDescription(null);
        assertValidationError(dto, "Description cannot be blank");

        dto = createValidServiceDto();
        dto.setDescription("D".repeat(501));
        assertValidationError(dto, "Description must be less than 500 characters");
    }

    @Test
    void shouldFailWhenServiceTypeIsNull() {
        ServiceDto dto = createValidServiceDto();
        dto.setServiceType(null);
        assertValidationError(dto, "Service type cannot be null");
    }

    @Test
    void shouldAcceptOptionalFields() {
        ServiceDto dto = createValidServiceDto();
        dto.setPrice(null);
        dto.setCurrency(null);
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldHandlePriceCorrectly() {
        ServiceDto dto = createValidServiceDto();
        dto.setPrice(BigDecimal.valueOf(-100));
        assertThat(validator.validate(dto)).isEmpty(); // Negative price allowed

        dto.setPrice(BigDecimal.ZERO);
        assertThat(validator.validate(dto)).isEmpty(); // Zero price allowed
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        ServiceDto dto1 = createServiceDtoWithId(id);
        ServiceDto dto2 = createServiceDtoWithId(id);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    private ServiceDto createValidServiceDto() {
        ServiceDto dto = new ServiceDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Guided Tour");
        dto.setDescription("Professional guide service");
        dto.setServiceType(ServiceType.GUIDE);
        dto.setPrice(BigDecimal.valueOf(50.0));
        dto.setCurrency("USD");
        dto.setAvailability(true);
        return dto;
    }

    private ServiceDto createServiceDtoWithId(UUID id) {
        ServiceDto dto = createValidServiceDto();
        dto.setId(id);
        return dto;
    }

    private void assertValidationError(ServiceDto dto, String message) {
        assertThat(validator.validate(dto))
                .extracting("message")
                .contains(message);
    }
}