package org.example.endtoend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Ride;
import org.example.entities.Status;
import org.example.integration.util.KafkaConsumer;
import org.example.repositories.RideRepository;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Optional;
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
    private RideRepository rideRepository;

    @Autowired
    private RideService rideService;

    @Autowired
    private KafkaConsumer kafkaConsumer;

    @Given("отправлен POST запрос на {string} с телом:")
    public void sendPostRequestWithBody(String path, String body) {
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

    @And("поездка сохранена в базе данных")
    public void checkRideInDatabase() {
        RideDTO ride = rideService.findById(100L);
        assertThat(ride.getPointA()).isEqualTo("Москва");
        assertThat(ride.getPointB()).isEqualTo("Санкт-Петербург");
    }

    @And("статус поездки равен {string}")
    public void checkRideStatus(String status) {
        RideDTO ride = rideService.findById(100L);
        assertThat(ride.getStatus()).isEqualTo(Status.valueOf(status));
    }

    @And("статус поездки изменен на {string}")
    public void checkRideStatusWithDriver(String status) {
        await().atMost(10, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(rideRepository.findByDriverIdAndStatus("driver1", status).isPresent()).isTrue();
                });
    }

    @Given("существует поездка с ID {long}")
    public void createRide(long id) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setPassengerId(1L);
        ride.setPointA("Москва");
        ride.setPointB("Санкт-Петербург");
        ride.setStatus(Status.WAITING);
        ride.setDeleted(false);
        rideRepository.save(ride);
    }

    @When("отправлен GET запрос на {string}")
    public void sendGetRequest(String path) {
        response = given()
                .when()
                .get(path);
    }

    @And("в ответе содержится поездка с ID {long}")
    public void checkRideId(long id) {
        RideDTO ride = response.as(RideDTO.class);
        assertThat(ride.getId()).isEqualTo(id);
    }

    @When("отправлен PATCH запрос на {string} с телом:")
    public void updateRide(String path, String body) {
        response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .patch(path);
    }

    @And("данные поездки обновлены в базе данных")
    public void checkUpdatedRideInDatabase() {
        Ride ride = rideRepository.findById(100L).orElseThrow();
        assertThat(ride.getPointA()).isEqualTo("Казань");
        assertThat(ride.getPointB()).isEqualTo("Екатеринбург");
    }

    @When("отправлен DELETE запрос на {string}")
    public void sendDeleteRequest(String path) {
        response = given()
                .when()
                .delete(path);
    }

    @And("поездка помечена как удаленная в базе данных")
    public void checkSoftDeleteInDatabase() {
        Ride ride = rideRepository.findById(100L).orElseThrow();
        assertThat(ride.isDeleted()).isTrue();
    }

    @Given("в системе существует {int} поездок")
    public void createRides(int amount) {
        for (int i = 1; i <= amount; i++) {
            Ride ride = new Ride();
            ride.setId(100L + i);
            ride.setPassengerId(1L);
            ride.setPointA("Москва");
            ride.setPointB("Санкт-Петербург");
            ride.setStatus(Status.WAITING);
            ride.setDeleted(false);
            rideRepository.save(ride);
        }
    }

    @And("в ответе содержится {int} поездок")
    public void checkRideCount(int count) {
        RidePageDTO ridePageDTO = response.as(RidePageDTO.class);
        assertThat(ridePageDTO.getRides()).hasSize(count);
    }

    @And("поле {string} равно {int}")
    public void checkFieldValue(String field, int value) {
        RidePageDTO ridePageDTO = response.as(RidePageDTO.class);
        switch (field) {
            case "totalElements" -> assertThat(ridePageDTO.getTotalElem()).isEqualTo(value);
            case "totalPages" -> assertThat(ridePageDTO.getTotalPages()).isEqualTo(value);
        }
    }

    @Given("существует поездка с ID {long} и статусом {string}")
    public void createRideWithStatus(long id, String status) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setPassengerId(1L);
        ride.setDriverId("driver1");
        ride.setPointA("Москва");
        ride.setPointB("Санкт-Петербург");
        ride.setStatus(Status.valueOf(status));
        ride.setDeleted(false);
        rideRepository.save(ride);
    }

    @And("событие {string} отправлено в Kafka")
    public void checkKafkaEvent(String eventType) {
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<TravelEvent> event = kafkaConsumer.getProcessedMessages(eventType);
                    assertThat(event).isPresent();
                });
    }

    @When("отправлен PATCH запрос на {string} с параметрами {string} и {int}")
    public void sendPatchRequestWithParams(String path, String rating, double cost) {
        response = given()
                .param("rating", rating)
                .param("cost", cost)
                .when()
                .patch(path);
    }
}