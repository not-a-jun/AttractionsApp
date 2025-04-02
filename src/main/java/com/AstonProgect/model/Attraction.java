package com.AstonProgect.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;
import java.util.UUID;

/**
 * Модель, представляющая достопримечательность.
 * <p>
 * Достопримечательность имеет название, описание, тип, а также связана с адресом,
 * услугами и информацией о билетах.
 * <p>
 * Связи:
 * <ul>
 *   <li>Многие к одному с Address (каждая достопримечательность имеет один адрес)</li>
 *   <li>Многие ко многим с Service (достопримечательность может предоставлять несколько услуг)</li>
 *   <li>Один к одному с TicketInfo (информация о билетах для данной достопримечательности)</li>
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

    @NotBlank(message = "Description cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Attraction type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttractionType attractionType;

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
