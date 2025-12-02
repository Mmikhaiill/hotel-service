package ru.example.hotel.api.service;

import jakarta.ejb.Remote;
import ru.example.hotel.api.dto.HotelDTO;
import ru.example.hotel.api.dto.PageRequest;
import ru.example.hotel.api.dto.PageResponse;

import java.util.Optional;

/**
 * Remote EJB интерфейс для работы с отелями
 */
@Remote
public interface HotelServiceRemote {

    /**
     * Получить список отелей с пагинацией
     * @param pageRequest параметры пагинации и сортировки
     * @return страница с отелями
     */
    PageResponse<HotelDTO> findAll(PageRequest pageRequest);

    /**
     * Найти отель по ID
     * @param id идентификатор отеля
     * @return отель или пустой Optional
     */
    Optional<HotelDTO> findById(Long id);

    /**
     * Создать новый отель
     * @param hotelDTO данные отеля
     * @return созданный отель с присвоенным ID
     */
    HotelDTO create(HotelDTO hotelDTO);

    /**
     * Обновить существующий отель
     * @param id идентификатор отеля
     * @param hotelDTO новые данные отеля
     * @return обновлённый отель
     */
    HotelDTO update(Long id, HotelDTO hotelDTO);

    /**
     * Удалить отель по ID
     * @param id идентификатор отеля
     * @return true если отель был удалён
     */
    boolean delete(Long id);

    /**
     * Получить общее количество отелей
     * @return количество отелей
     */
    long count();
}
