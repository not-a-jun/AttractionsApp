package com.AstonProgect.model;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TicketInfoTest {

    private final Validator validator;

    public TicketInfoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private TicketInfo createValidTicketInfo() {
        TicketInfo ticketInfo = new TicketInfo();
        ticketInfo.setPrice(new BigDecimal("20.50"));
        ticketInfo.setCurrency("EUR");
        ticketInfo.setAvailability(true);
        return ticketInfo;
    }

    @Test
    void shouldCreateValidTicketInfo() {
        TicketInfo ticketInfo = createValidTicketInfo();

        assertThat(validator.validate(ticketInfo)).isEmpty();
        assertThat(ticketInfo.getPrice()).isEqualTo(new BigDecimal("20.50"));
        assertThat(ticketInfo.getCurrency()).isEqualTo("EUR");
        assertThat(ticketInfo.isAvailability()).isTrue();
    }

    @Test
    void shouldFailWhenPriceIsInvalid() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setPrice(BigDecimal.ZERO);

        Set<ConstraintViolation<TicketInfo>> violations = validator.validate(ticketInfo);
        assertThat(violations)
                .extracting("message")
                .contains("Price must be greater than 0");
    }

    @Test
    void shouldFailWhenCurrencyIsInvalid() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setCurrency("US");

        Set<ConstraintViolation<TicketInfo>> violations = validator.validate(ticketInfo);
        assertThat(violations)
                .extracting("message")
                .contains("Currency must be exactly 3 characters");
    }

    @Test
    void shouldHaveDefaultAvailabilityValue() {
        TicketInfo ticketInfo = new TicketInfo();
        assertThat(ticketInfo.isAvailability()).isFalse();
    }

    @Test
    void shouldHandleNullAttraction() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setAttraction(null);
        assertThat(ticketInfo.getAttraction()).isNull();
    }

    @Test
    void shouldAcceptMinimalPrice() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setPrice(new BigDecimal("0.01"));
        assertThat(validator.validate(ticketInfo)).isEmpty();
    }

    @Test
    void shouldFailWhenPriceIsNull() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setPrice(null);

        Set<ConstraintViolation<TicketInfo>> violations = validator.validate(ticketInfo);
        assertThat(violations)
                .extracting("message")
                .contains("Price cannot be null");
    }

    @Test
    void shouldHandleNullCurrencyWhenNotValidated() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setCurrency(null);
        assertThat(ticketInfo.getCurrency()).isNull();
    }

    @Test
    void shouldHandleEdgePriceValues() {
        TicketInfo ticketInfo = createValidTicketInfo();

        // Максимальное значение BigDecimal
        ticketInfo.setPrice(new BigDecimal(Double.MAX_VALUE));
        assertThat(validator.validate(ticketInfo)).isEmpty();

        // Минимальное положительное значение
        ticketInfo.setPrice(new BigDecimal("0.01"));
        assertThat(validator.validate(ticketInfo)).isEmpty();
    }

    @Test
    void shouldHandleMaxPriceValue() {
        TicketInfo ticketInfo = createValidTicketInfo();
        ticketInfo.setPrice(new BigDecimal(Double.MAX_VALUE));

        assertThat(validator.validate(ticketInfo)).isEmpty();
    }

    @Test
    void shouldMaintainBidirectionalRelationship() {
        TicketInfo ticketInfo = createValidTicketInfo();
        Attraction attraction = new Attraction();

        ticketInfo.setAttraction(attraction);
        attraction.setTicketInfo(ticketInfo);

        assertThat(ticketInfo.getAttraction()).isEqualTo(attraction);
        assertThat(attraction.getTicketInfo()).isEqualTo(ticketInfo);
    }

    @Test
    void testEqualsAndHashCode() {
        TicketInfo ticket1 = createValidTicketInfo();
        TicketInfo ticket2 = createValidTicketInfo();
        ticket2.setId(ticket1.getId()); // Устанавливаем одинаковые ID

        assertThat(ticket1).isEqualTo(ticket2);
        assertThat(ticket1.hashCode()).isEqualTo(ticket2.hashCode());
    }

    @Test
    void testToString() {
        TicketInfo ticket = createValidTicketInfo();
        assertThat(ticket.toString()).isNotNull().contains(ticket.getCurrency());
    }

    @Test
    void shouldValidatePriceConstraints() {
        TicketInfo ticket = createValidTicketInfo();
        ticket.setPrice(new BigDecimal("0.00"));

        Set<ConstraintViolation<TicketInfo>> violations = validator.validate(ticket);
        assertThat(violations).isNotEmpty();
    }
}