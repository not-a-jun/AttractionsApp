package com.AstonProgect.dto;

import com.AstonProgect.model.AttractionType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class AttractionDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldValidateAllConstraints() {
        AttractionDto dto = createValidAttractionDto();
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailWhenNameConstraintsViolated() {
        AttractionDto dto = createValidAttractionDto();
        dto.setName(null);
        assertValidationError(dto, "Name cannot be blank");

        dto = createValidAttractionDto();
        dto.setName(" ");
        assertValidationError(dto, "Name cannot be blank");

        dto = createValidAttractionDto();
        dto.setName("A".repeat(101));
        assertValidationError(dto, "Name must be less than 100 characters");
    }

    @Test
    void shouldFailWhenDescriptionConstraintsViolated() {
        AttractionDto dto = createValidAttractionDto();
        dto.setDescription(null);
        assertValidationError(dto, "Description cannot be blank");

        dto = createValidAttractionDto();
        dto.setDescription("D".repeat(1001));
        assertValidationError(dto, "Description must be less than 1000 characters");
    }

    @Test
    void shouldFailWhenAttractionTypeIsNull() {
        AttractionDto dto = createValidAttractionDto();
        dto.setAttractionType(null);
        assertValidationError(dto, "Attraction type cannot be null");
    }

    @Test
    void shouldFailWhenAddressIdIsNull() {
        AttractionDto dto = createValidAttractionDto();
        dto.setAddressId(null);
        assertValidationError(dto, "Address ID cannot be null");
    }

    @Test
    void shouldAcceptNullOptionalFields() {
        AttractionDto dto = createValidAttractionDto();
        dto.setServiceIds(null);
        dto.setTicketInfoId(null);
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldImplementEqualsAndHashCodeBasedOnId() {
        UUID id = UUID.randomUUID();

        AttractionDto dto1 = new AttractionDto();
        dto1.setId(id);

        AttractionDto dto2 = new AttractionDto();
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
        AttractionDto dto1 = new AttractionDto();
        dto1.setId(UUID.randomUUID());

        AttractionDto dto2 = new AttractionDto();
        dto2.setId(UUID.randomUUID());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldHaveConsistentHashCode() {
        AttractionDto dto = new AttractionDto();
        dto.setId(UUID.randomUUID());
        int initialHashCode = dto.hashCode();

        // После повторного вызова hashCode должен остаться тем же
        assertThat(dto.hashCode()).isEqualTo(initialHashCode);
    }

    private AttractionDto createValidAttractionDto() {
        AttractionDto dto = new AttractionDto();
        dto.setId(UUID.randomUUID());
        dto.setName("Colosseum");
        dto.setDescription("Ancient amphitheater");
        dto.setAttractionType(AttractionType.NATURE_RESERVE);
        dto.setAddressId(UUID.randomUUID());
        dto.setServiceIds(Set.of(UUID.randomUUID()));
        return dto;
    }

    private AttractionDto createAttractionDtoWithId(UUID id) {
        AttractionDto dto = createValidAttractionDto();
        dto.setId(id);
        return dto;
    }

    private void assertValidationError(AttractionDto dto, String message) {
        assertThat(validator.validate(dto))
                .extracting("message")
                .contains(message);
    }
}