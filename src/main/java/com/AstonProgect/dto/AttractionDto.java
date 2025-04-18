package com.AstonProgect.dto;

import com.AstonProgect.model.AttractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

/**
 * DTO для представления достопримечательности.
 * <p>
 * Содержит основную информацию о достопримечательности, включая название, описание, тип,
 * а также связанные сущности (адрес, услуги, билеты).
 */
@Data
public class AttractionDto {
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Attraction type cannot be null")
    private AttractionType attractionType;

    @NotNull(message = "Address ID cannot be null")
    private UUID addressId;

    private Set<UUID> serviceIds;
    private UUID ticketInfoId;
}
