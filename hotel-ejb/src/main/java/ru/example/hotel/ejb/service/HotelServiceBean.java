package ru.example.hotel.ejb.service;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import ru.example.hotel.api.dto.HotelDTO;
import ru.example.hotel.api.dto.PageRequest;
import ru.example.hotel.api.dto.PageResponse;
import ru.example.hotel.api.service.HotelServiceLocal;
import ru.example.hotel.api.service.HotelServiceRemote;
import ru.example.hotel.ejb.dao.HotelDAO;
import ru.example.hotel.ejb.entity.Hotel;
import ru.example.hotel.ejb.mapper.HotelMapper;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с отелями
 * Реализует как Local, так и Remote интерфейсы EJB
 */
@Stateless
public class HotelServiceBean implements HotelServiceLocal, HotelServiceRemote {

    private static final Logger LOG = Logger.getLogger(HotelServiceBean.class.getName());

    @EJB
    private HotelDAO hotelDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PageResponse<HotelDTO> findAll(PageRequest pageRequest) {
        LOG.info("Finding all hotels with pagination: page=" + pageRequest.getPage() + 
                 ", size=" + pageRequest.getSize());

        boolean sortAscending = pageRequest.isSortAscending();
        
        List<Hotel> hotels = hotelDAO.findAll(
                pageRequest.getOffset(),
                pageRequest.getSize(),
                sortAscending
        );

        List<HotelDTO> dtos = hotels.stream()
                .map(HotelMapper::toDTO)
                .collect(Collectors.toList());

        long totalElements = hotelDAO.count();

        return PageResponse.of(dtos, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Optional<HotelDTO> findById(Long id) {
        LOG.info("Finding hotel by id: " + id);
        return hotelDAO.findById(id).map(HotelMapper::toDTO);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public HotelDTO create(HotelDTO hotelDTO) {
        LOG.info("Creating new hotel: " + hotelDTO.getName());
        
        Hotel hotel = HotelMapper.toEntity(hotelDTO);
        hotel.setId(null); // Убедиться, что ID null для новой записи
        if (hotel.getAddress() != null) {
            hotel.getAddress().setId(null);
        }
        
        Hotel saved = hotelDAO.save(hotel);
        return HotelMapper.toDTO(saved);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public HotelDTO update(Long id, HotelDTO hotelDTO) {
        LOG.info("Updating hotel with id: " + id);
        
        Optional<Hotel> existingOpt = hotelDAO.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Hotel not found with id: " + id);
        }
        
        Hotel existing = existingOpt.get();
        HotelMapper.updateEntity(existing, hotelDTO);
        
        Hotel updated = hotelDAO.update(existing);
        return HotelMapper.toDTO(updated);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean delete(Long id) {
        LOG.info("Deleting hotel with id: " + id);
        return hotelDAO.delete(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public long count() {
        return hotelDAO.count();
    }
}
