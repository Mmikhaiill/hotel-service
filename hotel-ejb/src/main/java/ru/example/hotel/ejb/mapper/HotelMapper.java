package ru.example.hotel.ejb.mapper;

import ru.example.hotel.api.dto.AddressDTO;
import ru.example.hotel.api.dto.HotelDTO;
import ru.example.hotel.ejb.entity.Address;
import ru.example.hotel.ejb.entity.Hotel;

/**
 * Маппер для преобразования между JPA сущностями и DTO
 */
public class HotelMapper {

    private HotelMapper() {
        // Utility class
    }

    /**
     * Преобразовать Address entity в AddressDTO
     */
    public static AddressDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .id(address.getId())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .street(address.getStreet())
                .building(address.getBuilding())
                .build();
    }

    /**
     * Преобразовать AddressDTO в Address entity
     */
    public static Address toEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        return Address.builder()
                .id(dto.getId())
                .postalCode(dto.getPostalCode())
                .city(dto.getCity())
                .street(dto.getStreet())
                .building(dto.getBuilding())
                .build();
    }

    /**
     * Преобразовать Hotel entity в HotelDTO
     */
    public static HotelDTO toDTO(Hotel hotel) {
        if (hotel == null) {
            return null;
        }
        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(toDTO(hotel.getAddress()))
                .category(hotel.getCategory())
                .notes(hotel.getNotes())
                .build();
    }

    /**
     * Преобразовать HotelDTO в Hotel entity
     */
    public static Hotel toEntity(HotelDTO dto) {
        if (dto == null) {
            return null;
        }
        Hotel hotel = Hotel.builder()
                .id(dto.getId())
                .name(dto.getName())
                .address(toEntity(dto.getAddress()))
                .category(dto.getCategory())
                .notes(dto.getNotes())
                .build();
        
        // Установить обратную связь
        if (hotel.getAddress() != null) {
            hotel.getAddress().setHotel(hotel);
        }
        
        return hotel;
    }

    /**
     * Обновить существующую сущность Hotel данными из DTO
     */
    public static void updateEntity(Hotel hotel, HotelDTO dto) {
        hotel.setName(dto.getName());
        hotel.setCategory(dto.getCategory());
        hotel.setNotes(dto.getNotes());
        
        // Обновить адрес
        if (dto.getAddress() != null) {
            Address address = hotel.getAddress();
            if (address == null) {
                address = new Address();
                hotel.setAddress(address);
                address.setHotel(hotel);
            }
            address.setPostalCode(dto.getAddress().getPostalCode());
            address.setCity(dto.getAddress().getCity());
            address.setStreet(dto.getAddress().getStreet());
            address.setBuilding(dto.getAddress().getBuilding());
        }
    }
}
