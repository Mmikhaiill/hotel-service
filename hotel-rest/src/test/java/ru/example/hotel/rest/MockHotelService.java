package ru.example.hotel.rest;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import ru.example.hotel.api.dto.*;
import ru.example.hotel.api.service.HotelServiceRemote;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Мок-реализация HotelServiceRemote для тестирования
 */
@Mock
@ApplicationScoped
public class MockHotelService implements HotelServiceRemote {

    private final Map<Long, HotelDTO> hotels = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MockHotelService() {
        // Добавить тестовые данные
        createTestData();
    }

    private void createTestData() {
        HotelDTO hotel1 = HotelDTO.builder()
                .name("Grand Hotel Moscow")
                .address(AddressDTO.builder()
                        .postalCode("101000")
                        .city("Москва")
                        .street("Тверская улица")
                        .building("15")
                        .build())
                .category(HotelCategory.FIVE_STARS)
                .notes("Роскошный отель")
                .build();
        create(hotel1);

        HotelDTO hotel2 = HotelDTO.builder()
                .name("Невский Палас")
                .address(AddressDTO.builder()
                        .postalCode("190000")
                        .city("Санкт-Петербург")
                        .street("Невский проспект")
                        .building("28")
                        .build())
                .category(HotelCategory.FOUR_STARS)
                .build();
        create(hotel2);
    }

    @Override
    public PageResponse<HotelDTO> findAll(PageRequest pageRequest) {
        List<HotelDTO> allHotels = new ArrayList<>(hotels.values());

        // Сортировка
        if (pageRequest.isSortAscending()) {
            allHotels.sort(Comparator.comparing(HotelDTO::getName));
        } else {
            allHotels.sort(Comparator.comparing(HotelDTO::getName).reversed());
        }

        // Пагинация
        int start = pageRequest.getOffset();
        int end = Math.min(start + pageRequest.getSize(), allHotels.size());

        List<HotelDTO> page = start < allHotels.size()
                ? allHotels.subList(start, end)
                : Collections.emptyList();

        return PageResponse.of(page, pageRequest.getPage(), pageRequest.getSize(), allHotels.size());
    }

    @Override
    public Optional<HotelDTO> findById(Long id) {
        return Optional.ofNullable(hotels.get(id));
    }

    @Override
    public HotelDTO create(HotelDTO hotelDTO) {
        Long id = idGenerator.getAndIncrement();
        hotelDTO.setId(id);
        if (hotelDTO.getAddress() != null) {
            hotelDTO.getAddress().setId(id);
        }
        hotels.put(id, hotelDTO);
        return hotelDTO;
    }

    @Override
    public HotelDTO update(Long id, HotelDTO hotelDTO) {
        if (!hotels.containsKey(id)) {
            throw new IllegalArgumentException("Hotel not found with id: " + id);
        }
        hotelDTO.setId(id);
        hotels.put(id, hotelDTO);
        return hotelDTO;
    }

    @Override
    public boolean delete(Long id) {
        return hotels.remove(id) != null;
    }

    @Override
    public long count() {
        return hotels.size();
    }

    // Метод для очистки данных в тестах
    public void clear() {
        hotels.clear();
        idGenerator.set(1);
    }
}
