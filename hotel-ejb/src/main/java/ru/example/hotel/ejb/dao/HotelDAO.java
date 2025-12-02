package ru.example.hotel.ejb.dao;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import ru.example.hotel.ejb.entity.Hotel;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) для работы с отелями
 * Реализован как Stateless EJB
 */
@Stateless
public class HotelDAO {

    @PersistenceContext(unitName = "hotelPU")
    private EntityManager em;

    /**
     * Найти все отели с пагинацией
     * @param offset смещение
     * @param limit количество записей
     * @param sortAscending порядок сортировки по названию
     * @return список отелей
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Hotel> findAll(int offset, int limit, boolean sortAscending) {
        String queryName = sortAscending ? "Hotel.findAll" : "Hotel.findAllDesc";
        TypedQuery<Hotel> query = em.createNamedQuery(queryName, Hotel.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Найти отель по ID
     * @param id идентификатор отеля
     * @return отель или пустой Optional
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Optional<Hotel> findById(Long id) {
        Hotel hotel = em.find(Hotel.class, id);
        if (hotel != null) {
            // Инициализировать ленивые связи
            hotel.getAddress();
        }
        return Optional.ofNullable(hotel);
    }

    /**
     * Сохранить новый отель
     * @param hotel отель для сохранения
     * @return сохранённый отель с присвоенным ID
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Hotel save(Hotel hotel) {
        em.persist(hotel);
        em.flush();
        return hotel;
    }

    /**
     * Обновить существующий отель
     * @param hotel отель для обновления
     * @return обновлённый отель
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Hotel update(Hotel hotel) {
        return em.merge(hotel);
    }

    /**
     * Удалить отель по ID
     * @param id идентификатор отеля
     * @return true если отель был удалён
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean delete(Long id) {
        Hotel hotel = em.find(Hotel.class, id);
        if (hotel != null) {
            em.remove(hotel);
            return true;
        }
        return false;
    }

    /**
     * Получить общее количество отелей
     * @return количество отелей
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public long count() {
        return em.createNamedQuery("Hotel.count", Long.class).getSingleResult();
    }
}
