package com.AstonProgect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Модель, представляющая информацию о билетах для достопримечательности.
 * <p>
 * Содержит данные о стоимости, валюте и доступности билетов.
 * <p>
 * Связи:
 * <ul>
 *   <li>Один к одному с Attraction (каждая информация о билетах связана с одной достопримечательностью)</li>
 * </ul>
 */
@Data
@Entity
public class TicketInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Currency cannot be blank")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    @Column(nullable = false, length = 3)
    private String currency;

    @NotNull(message = "Availability cannot be null")
    @Column(nullable = false)
    private boolean availability;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;
}
