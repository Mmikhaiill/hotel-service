package ru.example.hotel.ejb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.example.hotel.api.dto.HotelCategory;

/**
 * JPA сущность для отеля
 */
@Entity
@Table(name = "hotels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@NamedQueries({
    @NamedQuery(
        name = "Hotel.findAll",
        query = "SELECT h FROM Hotel h LEFT JOIN FETCH h.address ORDER BY h.name ASC"
    ),
    @NamedQuery(
        name = "Hotel.findAllDesc",
        query = "SELECT h FROM Hotel h LEFT JOIN FETCH h.address ORDER BY h.name DESC"
    ),
    @NamedQuery(
        name = "Hotel.count",
        query = "SELECT COUNT(h) FROM Hotel h"
    )
})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20)
    private HotelCategory category;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
