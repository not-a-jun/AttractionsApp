package com.AstonProgect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * Модель, представляющая услугу для достопримечательности.
 * <p>
 * Услуга содержит информацию о названии, описании, типе, цене,
 * валюте и доступности.
 * <p>
 * Связи:
 * <ul>
 *   <li>Многие ко многим с Attraction (одна услуга может быть доступна для нескольких достопримечательностей)</li>
 * </ul>
 */
@Data
@Entity
@Table(name = "services")
public class Service {

    @Id
    @Column(name = "service_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Description cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Service type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    @Column(length = 3)
    private String currency;

    private boolean availability = true;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private Set<Attraction> attractions;
}