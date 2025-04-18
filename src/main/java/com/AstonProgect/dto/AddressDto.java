package com.AstonProgect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.UUID;

/**
 * DTO для представления адреса.
 * <p>
 * Содержит информацию о местоположении, включая город, улицу, здание и географические координаты.
 * Все поля, кроме идентификатора, обязательны для заполнения.
 */
@Data
public class AddressDto {
    private UUID id;

    @NotNull(message = "Building cannot be null")
    @Positive(message = "Building number must be positive")
    private Integer building;

    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "Region cannot be blank")
    private String region;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;
}
