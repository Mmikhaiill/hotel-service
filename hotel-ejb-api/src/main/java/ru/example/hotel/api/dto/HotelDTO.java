package ru.example.hotel.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO для отеля
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private AddressDTO address;

    private HotelCategory category;

    private String notes;
}
