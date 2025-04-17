package com.AstonProgect.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class TicketInfoDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateAllConstraints() {
        TicketInfoDto dto = createValidTicketInfoDto();
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailWhenPriceConstraintsViolated() {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setPrice(null);
        assertValidationError(dto, "Price cannot be null");

        dto = createValidTicketInfoDto();
        dto.setPrice(BigDecimal.ZERO);
        assertValidationError(dto, "Price must be greater than 0");
    }

    @Test
    void shouldFailWhenCurrencyConstraintsViolated() {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setCurrency(null);
        assertValidationError(dto, "Currency cannot be blank");

        dto = createValidTicketInfoDto();
        dto.setCurrency(" ");
        assertValidationError(dto, "Currency cannot be blank");

        dto = createValidTicketInfoDto();
        dto.setCurrency("US");
        assertValidationError(dto, "Currency must be 3 characters");

        dto = createValidTicketInfoDto();
        dto.setCurrency("USDD");
        assertValidationError(dto, "Currency must be 3 characters");
    }

    @Test
    void shouldFailWhenAvailabilityIsNull() {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setAvailability(null);
        assertValidationError(dto, "Availability cannot be null");
    }

    @Test
    void shouldFailWhenAttractionIdIsNull() {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setAttractionId(null);
        assertValidationError(dto, "Attraction ID cannot be null");
    }

    @Test
    void shouldAcceptMinimalPrice() {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setPrice(BigDecimal.valueOf(0.01));
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldImplementEqualsAndHashCodeBasedOnId() {
        UUID id = UUID.randomUUID();

        TicketInfoDto dto1 = new TicketInfoDto();
        dto1.setId(id);

        TicketInfoDto dto2 = new TicketInfoDto();
        dto2.setId(id);

        // Проверяем рефлексивность
        assertThat(dto1).isEqualTo(dto1);

        // Проверяем симметричность
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);

        // Проверяем hashCode
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());

        // Проверяем null
        assertThat(dto1).isNotEqualTo(null);

        // Проверяем другой класс
        assertThat(dto1).isNotEqualTo(new Object());
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        TicketInfoDto dto1 = new TicketInfoDto();
        dto1.setId(UUID.randomUUID());

        TicketInfoDto dto2 = new TicketInfoDto();
        dto2.setId(UUID.randomUUID());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        TicketInfoDto dto = new TicketInfoDto();
        dto.setId(UUID.randomUUID());
        int initialHashCode = dto.hashCode();

        // После повторного вызова hashCode должен остаться тем же
        assertThat(dto.hashCode()).isEqualTo(initialHashCode);
    }

    private TicketInfoDto createValidTicketInfoDto() {
        TicketInfoDto dto = new TicketInfoDto();
        dto.setId(UUID.randomUUID());
        dto.setPrice(BigDecimal.valueOf(50.0));
        dto.setCurrency("USD");
        dto.setAvailability(true);
        dto.setAttractionId(UUID.randomUUID());
        return dto;
    }

    private TicketInfoDto createTicketInfoDtoWithId(UUID id) {
        TicketInfoDto dto = createValidTicketInfoDto();
        dto.setId(id);
        return dto;
    }

    private void assertValidationError(TicketInfoDto dto, String message) {
        assertThat(validator.validate(dto))
                .extracting("message")
                .contains(message);
    }
}