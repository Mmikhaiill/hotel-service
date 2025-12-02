package ru.example.hotel.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO для адреса отеля
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;

    private String postalCode;

    private String city;

    private String street;

    private String building;
}
