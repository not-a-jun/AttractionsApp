package com.AstonProgect.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

/**
 * Сущность, представляющая туристическую достопримечательность.
 * <p>
 * Содержит:
 * <ul>
 *   <li>Основную информацию (название, описание)</li>
 *   <li>Тип достопримечательности</li>
 *   <li>Связанные сущности (адрес, услуги, билеты)</li>
 * </ul>
 *
 * <p>Связи:
 * <ul>
 *   <li>Многие-к-одному с {@link Address} - каждая достопримечательность имеет один адрес</li>
 *   <li>Многие-ко-многим с {@link Service} - может предоставлять несколько услуг</li>
 *   <li>Один-к-одному с {@link TicketInfo} - информация о билетах</li>
 * </ul>
 */
@Data
@Entity
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    @NotBlank(message = "Description cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Attraction type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttractionType attractionType;

    @Valid
    @NotNull(message = "Address cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id",nullable = false)
    private Address address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "attraction_service",
            joinColumns = @JoinColumn(name = "attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @JsonManagedReference
    private Set<Service> services;

    @OneToOne(mappedBy = "attraction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TicketInfo ticketInfo;
}
