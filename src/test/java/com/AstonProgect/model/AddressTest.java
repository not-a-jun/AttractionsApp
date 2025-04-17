package com.AstonProgect.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    private final Validator validator;

    public AddressTest() {
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

    @Test
    void shouldCreateValidAddress() {
        Address address = createValidAddress();

        assertThat(address.getBuilding()).isEqualTo(10);
        assertThat(address.getStreet()).isEqualTo("Main Street");
        assertThat(address.getCity()).isEqualTo("New York");
        assertThat(address.getRegion()).isEqualTo("NY");
        assertThat(validator.validate(address)).isEmpty();
    }

    @Test
    void shouldFailValidationWhenFieldsAreBlankOrNull() {
        Address address = new Address();
        address.setBuilding(null);
        address.setStreet("");
        address.setCity("");
        address.setRegion("");

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(4);
        assertThat(violations)
                .extracting("message")
                .contains(
                        "Building cannot be null",
                        "Street cannot be blank",
                        "City cannot be blank",
                        "Region cannot be blank"
                );
    }

    @Test
    void shouldFailWhenBuildingIsInvalid() {
        Address address = createValidAddress();
        address.setBuilding(0);

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations)
                .extracting("message")
                .contains("Building number must be positive");
    }

    @Test
    void shouldFailWhenCoordinatesAreInvalid() {
        Address address = createValidAddress();
        address.setLatitude(-91.0);
        address.setLongitude(181.0);

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations)
                .extracting("message")
                .contains(
                        "Latitude must be between -90 and 90",
                        "Longitude must be between -180 and 180"
                );
    }

    @Test
    void shouldManageAttractionsList() {
        Address address = createValidAddress();
        Attraction attraction = new Attraction();
        attraction.setName("Statue of Liberty");

        address.setAttractions(new ArrayList<>());
        address.getAttractions().add(attraction);

        assertThat(address.getAttractions())
                .hasSize(1)
                .first()
                .extracting(Attraction::getName)
                .isEqualTo("Statue of Liberty");
    }

    @Test
    void shouldHandleNullAttractionsList() {
        Address address = createValidAddress();
        address.setAttractions(null);
        assertThat(address.getAttractions()).isNull();
    }

    @Test
    void shouldMaintainBidirectionalRelationship() {
        Address address = createValidAddress();
        Attraction attraction = new Attraction();

        address.setAttractions(List.of(attraction));
        attraction.setAddress(address);

        assertThat(address.getAttractions()).containsExactly(attraction);
        assertThat(attraction.getAddress()).isEqualTo(address);
    }

    @Test
    void shouldGenerateIdAutomatically() {
        Address address = new Address();
        assertThat(address.getId()).isNull();
    }

    @Test
    void shouldHandleNullCoordinates() {
        Address address = createValidAddress();
        address.setLatitude(null);
        address.setLongitude(null);

        assertThat(validator.validate(address)).isEmpty();
        assertThat(address.getLatitude()).isNull();
        assertThat(address.getLongitude()).isNull();
    }

    @Test
    void shouldHandleEdgeCoordinateValues() {
        Address address = createValidAddress();

        // Проверка граничных допустимых значений
        address.setLatitude(90.0);
        address.setLongitude(180.0);
        assertThat(validator.validate(address)).isEmpty();

        address.setLatitude(-90.0);
        address.setLongitude(-180.0);
        assertThat(validator.validate(address)).isEmpty();
    }

    @Test
    void shouldValidateCoordinatePrecision() {
        Address address = createValidAddress();
        address.setLatitude(40.71281234);
        address.setLongitude(-74.00601234);

        assertThat(validator.validate(address)).isEmpty();
        assertThat(address.getLatitude()).isEqualTo(40.71281234);
        assertThat(address.getLongitude()).isEqualTo(-74.00601234);
    }

    @Test
    void shouldHandleBuildingNumberBoundaries() {
        Address address = createValidAddress();

        // Корректные граничные значения
        address.setBuilding(1); // минимальное допустимое
        assertThat(validator.validate(address)).isEmpty();

        address.setBuilding(Integer.MAX_VALUE); // максимальное для int
        assertThat(validator.validate(address)).isEmpty();
    }

    @Test
    void shouldHandleMaxIntegerBuildingValue() {
        Address address = createValidAddress();
        address.setBuilding(Integer.MAX_VALUE);

        assertThat(validator.validate(address)).isEmpty();
        assertThat(address.getBuilding()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void shouldHandleMultipleAttractions() {
        Address address = createValidAddress();
        Attraction attraction1 = new Attraction();
        Attraction attraction2 = new Attraction();

        address.setAttractions(List.of(attraction1, attraction2));
        assertThat(address.getAttractions()).hasSize(2);
    }

    @Test
    void testEqualsAndHashCode() {
        Address address1 = createValidAddress();
        Address address2 = createValidAddress();
        address2.setId(address1.getId());

        assertThat(address1).isEqualTo(address2);
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
    }

    @Test
    void testCoordinateValidation() {
        Address address = createValidAddress();
        address.setLatitude(91.0); // Некорректное значение

        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).isNotEmpty();
    }
}