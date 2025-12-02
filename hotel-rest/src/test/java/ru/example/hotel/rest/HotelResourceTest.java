package ru.example.hotel.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import ru.example.hotel.api.dto.AddressDTO;
import ru.example.hotel.api.dto.HotelCategory;
import ru.example.hotel.api.dto.HotelDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

/**
 * Интеграционные тесты для HotelResource
 */
@QuarkusTest
class HotelResourceTest {

    @Test
    void testGetAllHotels() {
        given()
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("content", hasSize(greaterThan(0)))
                .body("page", equalTo(0))
                .body("totalElements", greaterThan(0));
    }

    @Test
    void testGetAllHotelsWithPagination() {
        given()
                .queryParam("page", 0)
                .queryParam("size", 1)
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content", hasSize(1))
                .body("size", equalTo(1));
    }

    @Test
    void testGetAllHotelsWithSortDesc() {
        given()
                .queryParam("sort", "desc")
                .when().get("/api/v1/hotels")
                .then()
                .statusCode(200)
                .body("content[0].name", notNullValue());
    }

    @Test
    void testGetHotelById() {
        given()
                .when().get("/api/v1/hotels/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", notNullValue())
                .body("address", notNullValue())
                .body("address.city", notNullValue());
    }

    @Test
    void testGetHotelByIdNotFound() {
        given()
                .when().get("/api/v1/hotels/999")
                .then()
                .statusCode(404)
                .body("message", containsString("не найден"));
    }

    @Test
    void testCreateHotel() {
        HotelDTO newHotel = HotelDTO.builder()
                .name("Test Hotel")
                .address(AddressDTO.builder()
                        .city("Тест Город")
                        .street("Тест Улица")
                        .building("1")
                        .build())
                .category(HotelCategory.THREE_STARS)
                .notes("Test notes")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(newHotel)
                .when().post("/api/v1/hotels")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Test Hotel"))
                .body("category", equalTo("THREE_STARS"));
    }

    @Test
    void testCreateHotelValidationError() {
        HotelDTO invalidHotel = HotelDTO.builder()
                .name("") // Пустое имя - ошибка валидации
                .address(AddressDTO.builder()
                        .city("City")
                        .street("Street")
                        .building("1")
                        .build())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(invalidHotel)
                .when().post("/api/v1/hotels")
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateHotelWithoutAddress() {
        HotelDTO hotelWithoutAddress = HotelDTO.builder()
                .name("Hotel Without Address")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(hotelWithoutAddress)
                .when().post("/api/v1/hotels")
                .then()
                .statusCode(400);
    }

    @Test
    void testUpdateHotel() {
        // Сначала создаём отель
        HotelDTO newHotel = HotelDTO.builder()
                .name("Hotel to Update")
                .address(AddressDTO.builder()
                        .city("City")
                        .street("Street")
                        .building("1")
                        .build())
                .category(HotelCategory.THREE_STARS)
                .build();

        Integer hotelId = given()
                .contentType(ContentType.JSON)
                .body(newHotel)
                .when().post("/api/v1/hotels")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Обновляем отель
        HotelDTO updatedHotel = HotelDTO.builder()
                .name("Updated Hotel Name")
                .address(AddressDTO.builder()
                        .city("New City")
                        .street("New Street")
                        .building("2")
                        .build())
                .category(HotelCategory.FIVE_STARS)
                .notes("Updated notes")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(updatedHotel)
                .when().put("/api/v1/hotels/" + hotelId)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Hotel Name"))
                .body("category", equalTo("FIVE_STARS"))
                .body("address.city", equalTo("New City"));
    }

    @Test
    void testUpdateHotelNotFound() {
        HotelDTO hotel = HotelDTO.builder()
                .name("Hotel")
                .address(AddressDTO.builder()
                        .city("City")
                        .street("Street")
                        .building("1")
                        .build())
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when().put("/api/v1/hotels/999")
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteHotel() {
        // Создаём отель для удаления
        HotelDTO newHotel = HotelDTO.builder()
                .name("Hotel to Delete")
                .address(AddressDTO.builder()
                        .city("City")
                        .street("Street")
                        .building("1")
                        .build())
                .build();

        Integer hotelId = given()
                .contentType(ContentType.JSON)
                .body(newHotel)
                .when().post("/api/v1/hotels")
                .then()
                .statusCode(201)
                .extract().path("id");

        // Удаляем отель
        given()
                .when().delete("/api/v1/hotels/" + hotelId)
                .then()
                .statusCode(204);

        // Проверяем, что отель удалён
        given()
                .when().get("/api/v1/hotels/" + hotelId)
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteHotelNotFound() {
        given()
                .when().delete("/api/v1/hotels/9999")
                .then()
                .statusCode(404);
    }

    @Test
    void testHealthEndpoint() {
        given()
                .when().get("/health")
                .then()
                .statusCode(200);
    }

    @Test
    void testOpenApiEndpoint() {
        given()
                .when().get("/openapi")
                .then()
                .statusCode(200)
                .contentType(containsString("application"));
    }
}
