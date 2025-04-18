package com.AstonProgect.dto;

import com.AstonProgect.model.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO для представления услуги.
 * <p>
 * Содержит информацию об услуге, включая название, описание, тип, цену и доступность.
 */
@Data
public class ServiceDto {
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 50, message = "Name must be less than 50 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Service type cannot be null")
    private ServiceType serviceType;

    private BigDecimal price;
    private String currency;
    private boolean availability;
}
