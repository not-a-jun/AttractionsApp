package com.AstonProgect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

/**
 * Модель, представляющая адрес.
 * <p>
 * Адрес содержит информацию о местоположении, включая город, улицу, дом,
 * регион, а также дополнительно координаты (долгота и широта).
 * <p>
 * Связи:
 * <ul>
 *   <li>Один ко многим с Attraction (по одному адресу может быть несколько достопримечательностей)</li>
 * </ul>
 */
@Data
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Building cannot be null")
    private Integer building;

    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "Region cannot be blank")
    private String region;

    @Column(nullable = true)
    private Double longitude;

    @Column(nullable = true)
    private Double latitude;

    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List <Attraction> attractions;
}
