package org.example.endtoend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.integration.util.KafkaConsumer;
import org.example.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndSteps {
    private Response response;

    @LocalServerPort
    private int port;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Given("отправлен POST запрос на {string} с телом:")
    public void sendPostRequest(String path, String body) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(path);
    }

    @Then("статус ответа должен быть {int}")
    public void checkResponseStatus(int statusCode) {
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @And("в ответе содержится созданный пассажир с полем {string} равным {string}")
    public void checkResponseField(String field, String value) {
        PassengerDTO passenger = response.as(PassengerDTO.class);
        assertThat(passenger.getStatus()).isEqualTo(Status.valueOf(value));
    }

    @And("пассажир сохранен в базе данных")
    public void checkPassengerInDatabase() {
        PassengerDTO passenger = passengerService.findOne(response.as(PassengerDTO.class).getId());
        assertThat(passenger.getName()).isEqualTo("Иван Иванов");
    }

    @And("событие {string} отправлено в Kafka")
    public void checkKafkaEvent(String eventType) {
        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(kafkaConsumer.getProcessedMessages(eventType).isPresent()).isTrue());
    }

    @Given("существует пассажир с ID {long}")
    public void createPassenger(long id) {
        Passenger passenger = new Passenger();
        passenger.setId(id);
        passenger.setName("Иван Иванов");
        passenger.setEmail("ivan@example.com");
        passenger.setPhoneNumber("+79991234567");
        passenger.setDeleted(false);
        passenger.setStatus(Status.NOT_ACTIVE);
        passengerService.save(passengerService.mapToDTO(passenger));
    }

    @When("отправлен PATCH запрос на {string} с телом:")
    public void updatePassenger(String path, String body) {
        response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .patch(path);
    }

    @And("данные пассажира обновлены в базе данных")
    public void checkUpdatedPassengerInDatabase() {
        PassengerDTO passenger = passengerService.findOne(100L);
        assertThat(passenger.getName()).isEqualTo("Новое Имя");
        assertThat(passenger.getEmail()).isEqualTo("new@example.com");
    }

    @When("отправлен GET запрос на {string}")
    public void sendGetRequest(String path) {
        response = given()
                .when()
                .get(path);
    }

    @And("в ответе содержится {int} пассажиров")
    public void checkPassengerCount(int count) {
        PassengerPageDTO passengerPageDTO = response.as(PassengerPageDTO.class);
        assertThat(passengerPageDTO.getPassengers()).hasSize(count);
    }

    @And("поле {string} равно {int}")
    public void checkFieldValue(String field, int value) {
        PassengerPageDTO passengerPageDTO = response.as(PassengerPageDTO.class);
        switch (field) {
            case "totalElements" -> assertThat(passengerPageDTO.getTotalElements()).isEqualTo(value);
            case "totalPages" -> assertThat(passengerPageDTO.getTotalPages()).isEqualTo(value);
        }
    }

    @When("отправлен DELETE запрос на {string}")
    public void sendDeleteRequest(String path) {
        response = given()
                .when()
                .delete(path);
    }

    @And("пассажир помечен как удаленный в базе данных")
    public void checkSoftDeleteInDatabase() {
        PassengerDTO passenger = passengerService.findOne(100L);
        assertThat(passenger.isDeleted()).isTrue();
    }

    @Given("в системе существует {int} пассажиров")
    public void createPassengers(int amount) {
        for (int i = 1; i <= amount; i++) {
            PassengerDTO passenger = new PassengerDTO();
            passenger.setName("Пассажир " + i);
            passenger.setEmail("passenger" + i + "@example.com");
            passenger.setPhoneNumber("+7999000000" + i);
            passenger.setStatus(Status.NOT_ACTIVE);
            passengerService.save(passenger);
        }
    }

    @Then("в ответе содержится пассажир с полем id равным {long}")
    public void checkPassengerId(long id) {
        PassengerDTO passenger = response.as(PassengerDTO.class);
        assertThat(passenger.getId()).isEqualTo(id);
    }

    @Given("существует пассажир с ID {long} в статусе {string}")
    public void createPassengerWithStatus(long id, String status) {
        Passenger passenger = new Passenger();
        passenger.setId(id);
        passenger.setName("Иван Иванов");
        passenger.setEmail("ivan@example.com");
        passenger.setPhoneNumber("+79991234567");
        passenger.setDeleted(false);
        passenger.setStatus(Status.valueOf(status));
        passengerService.save(passengerService.mapToDTO(passenger));
    }

    @Then("статус пассажира изменен на {string}")
    public void checkPassengerStatus(String status) {
        PassengerDTO passenger = passengerService.findOne(100L);
        assertThat(passenger.getStatus()).isEqualTo(Status.valueOf(status));
    }

    @When("отправлен PATCH запрос на {string} с passenger id {long}, pointA {string} и pointB {string}")
    public void sendOrderTaxiRequest(String path, long passengerId, String pointA, String pointB) {
        response = given()
                .queryParam("pointA", pointA)
                .queryParam("pointB", pointB)
                .when()
                .patch(path + "/" + passengerId);
    }
}