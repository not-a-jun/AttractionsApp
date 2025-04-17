package com.AstonProgect.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class AddressDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassValidationWithCorrectData() {
        AddressDto dto = createValidAddressDto();
        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldFailWhenBuildingIsNotPositive() {
        AddressDto dto = createValidAddressDto();
        dto.setBuilding(0);
        assertValidationError(dto, "Building number must be positive");
    }

    @Test
    void shouldFailWhenRequiredFieldsAreNull() {
        AddressDto dto = new AddressDto();
        assertThat(validator.validate(dto)).hasSize(6);
    }

    @Test
    void shouldFailWhenStringFieldsAreBlank() {
        AddressDto dto = createValidAddressDto();
        dto.setStreet(" ");
        assertValidationError(dto, "Street cannot be blank");

        dto = createValidAddressDto();
        dto.setCity(" ");
        assertValidationError(dto, "City cannot be blank");

        dto = createValidAddressDto();
        dto.setRegion(" ");
        assertValidationError(dto, "Region cannot be blank");
    }

    @Test
    void shouldFailWhenCoordinatesAreNull() {
        AddressDto dto = createValidAddressDto();
        dto.setLongitude(null);
        dto.setLatitude(null);
        assertThat(validator.validate(dto))
                .extracting("message")
                .containsExactlyInAnyOrder(
                        "Longitude cannot be null",
                        "Latitude cannot be null"
                );
    }

    @Test
    void shouldImplementEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        AddressDto dto1 = createAddressDtoWithId(id);
        AddressDto dto2 = createAddressDtoWithId(id);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldIncludeFieldsInToString() {
        AddressDto dto = createValidAddressDto();
        assertThat(dto.toString())
                .contains("Main Street")
                .contains("New York");
    }

    private AddressDto createValidAddressDto() {
        AddressDto dto = new AddressDto();
        dto.setId(UUID.randomUUID());
        dto.setBuilding(10);
        dto.setStreet("Main Street");
        dto.setCity("New York");
        dto.setRegion("NY");
        dto.setLongitude(40.7128);
        dto.setLatitude(-74.0060);
        return dto;
    }

    private AddressDto createAddressDtoWithId(UUID id) {
        AddressDto dto = createValidAddressDto();
        dto.setId(id);
        return dto;
    }

    private void assertValidationError(AddressDto dto, String message) {
        assertThat(validator.validate(dto))
                .extracting("message")
                .contains(message);
    }
}