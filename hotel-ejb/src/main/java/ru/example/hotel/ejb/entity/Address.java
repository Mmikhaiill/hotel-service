package ru.example.hotel.ejb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * JPA сущность для адреса отеля
 */
@Entity
@Table(name = "addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @NotBlank
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @NotBlank
    @Column(name = "building", nullable = false, length = 50)
    private String building;

    @OneToOne(mappedBy = "address")
    @ToString.Exclude
    private Hotel hotel;
}
