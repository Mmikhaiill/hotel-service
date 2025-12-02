package ru.example.hotel.rest.resource;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import ru.example.hotel.api.dto.HotelDTO;
import ru.example.hotel.api.dto.PageRequest;
import ru.example.hotel.api.dto.PageResponse;
import ru.example.hotel.api.service.HotelServiceRemote;
import ru.example.hotel.rest.exception.ErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * REST API ресурс для работы с отелями
 */
@Path("/api/v1/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Hotels", description = "API для управления отелями")
public class HotelResource {

    private static final Logger LOG = Logger.getLogger(HotelResource.class.getName());

    @Inject
    HotelServiceRemote hotelService;

    /**
     * Получить список отелей с пагинацией
     */
    @GET
    @Operation(summary = "Получить список отелей",
            description = "Возвращает постраничный список отелей с возможностью сортировки")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Успешный ответ",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = PageResponse.class)
                    )
            )
    })
    public Response getAllHotels(
            @Parameter(description = "Номер страницы (начиная с 0)")
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,

            @Parameter(description = "Размер страницы")
            @QueryParam("size") @DefaultValue("10") @Min(1) @Max(100) int size,

            @Parameter(description = "Порядок сортировки по названию (asc/desc)")
            @QueryParam("sort") @DefaultValue("asc") String sort) {

        LOG.info("GET /api/v1/hotels - page=" + page + ", size=" + size + ", sort=" + sort);

        boolean sortAscending = !"desc".equalsIgnoreCase(sort);

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortField("name")
                .sortAscending(sortAscending)
                .build();

        PageResponse<HotelDTO> response = hotelService.findAll(pageRequest);

        return Response.ok(response).build();
    }

    /**
     * Получить отель по ID
     */
    @GET
    @Path("/{id}")
    @Operation(summary = "Получить отель по ID",
            description = "Возвращает информацию об отеле по его идентификатору")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Отель найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = HotelDTO.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Отель не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response getHotelById(
            @Parameter(description = "ID отеля", required = true)
            @PathParam("id") Long id) {

        LOG.info("GET /api/v1/hotels/" + id);

        Optional<HotelDTO> hotel = hotelService.findById(id);

        if (hotel.isPresent()) {
            return Response.ok(hotel.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.of(404, "Отель с ID " + id + " не найден"))
                    .build();
        }
    }

    /**
     * Создать новый отель
     */
    @POST
    @Operation(summary = "Создать отель",
            description = "Создаёт новый отель с указанными данными")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Отель создан",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = HotelDTO.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Некорректные данные",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response createHotel(HotelDTO hotelDTO) {
        LOG.info("POST /api/v1/hotels - Creating hotel: " + (hotelDTO != null ? hotelDTO.getName() : "null"));

        // Ручная валидация
        List<String> errors = validateHotel(hotelDTO);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.of(400, "Ошибка валидации", errors))
                    .build();
        }

        HotelDTO created = hotelService.create(hotelDTO);

        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    /**
     * Обновить существующий отель
     */
    @PUT
    @Path("/{id}")
    @Operation(summary = "Обновить отель",
            description = "Обновляет данные существующего отеля")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Отель обновлён",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = HotelDTO.class)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Отель не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Некорректные данные",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response updateHotel(
            @Parameter(description = "ID отеля", required = true)
            @PathParam("id") Long id,
            HotelDTO hotelDTO) {

        LOG.info("PUT /api/v1/hotels/" + id);

        // Ручная валидация
        List<String> errors = validateHotel(hotelDTO);
        if (!errors.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErrorResponse.of(400, "Ошибка валидации", errors))
                    .build();
        }

        try {
            HotelDTO updated = hotelService.update(id, hotelDTO);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.of(404, e.getMessage()))
                    .build();
        }
    }

    /**
     * Удалить отель
     */
    @DELETE
    @Path("/{id}")
    @Operation(summary = "Удалить отель",
            description = "Удаляет отель по его идентификатору")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Отель удалён"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Отель не найден",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public Response deleteHotel(
            @Parameter(description = "ID отеля", required = true)
            @PathParam("id") Long id) {

        LOG.info("DELETE /api/v1/hotels/" + id);

        boolean deleted = hotelService.delete(id);

        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.of(404, "Отель с ID " + id + " не найден"))
                    .build();
        }
    }

    /**
     * Ручная валидация HotelDTO
     */
    private List<String> validateHotel(HotelDTO hotel) {
        List<String> errors = new ArrayList<>();

        if (hotel == null) {
            errors.add("Данные отеля не могут быть пустыми");
            return errors;
        }

        if (hotel.getName() == null || hotel.getName().trim().isEmpty()) {
            errors.add("Название отеля обязательно для заполнения");
        }

        if (hotel.getAddress() == null) {
            errors.add("Адрес отеля обязателен для заполнения");
        } else {
            if (hotel.getAddress().getCity() == null || hotel.getAddress().getCity().trim().isEmpty()) {
                errors.add("Город обязателен для заполнения");
            }
            if (hotel.getAddress().getStreet() == null || hotel.getAddress().getStreet().trim().isEmpty()) {
                errors.add("Улица обязательна для заполнения");
            }
            if (hotel.getAddress().getBuilding() == null || hotel.getAddress().getBuilding().trim().isEmpty()) {
                errors.add("Номер дома обязателен для заполнения");
            }
        }

        return errors;
    }
}
