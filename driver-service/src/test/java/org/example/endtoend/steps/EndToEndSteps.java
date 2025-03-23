package org.example.endtoend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.dto.*;
import org.example.integration.util.KafkaConsumer;
import org.example.repositories.DriverRepository;
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
    private DriverRepository driverRepository;

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

    @And("водитель сохранен в базе данных")
    public void checkDriverInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.getName()).isEqualTo("Иван Иванов");
    }

    @And("событие {string} отправлено в Kafka")
    public void checkKafkaEvent(String eventType) {
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Optional<TravelEvent> event = kafkaConsumer.getProcessedMessages(eventType);
                    assertThat(event).isPresent();
                });
    }

    @Given("существует водитель с ID {string}")
    public void createDriver(String id) {
        Driver driver = new Driver();
        driver.setId(id);
        driver.setName("Иван Иванов");
        driver.setExperience(5);
        driver.setPhone("+79991234567");
        driver.setEmail("ivan@example.com");
        driver.setDeleted(false);
        driver.setStatus("FREE");
        driverRepository.save(driver);
    }

    @When("отправлен PATCH запрос на {string} с телом:")
    public void updateDriver(String path, String body) {
        response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .patch(path);
    }

    @And("данные водителя обновлены в базе данных")
    public void checkUpdatedDriverInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.getName()).isEqualTo("Петр Петров");
    }

    @When("отправлен DELETE запрос на {string}")
    public void sendDeleteRequest(String path) {
        response = given()
                .when()
                .delete(path);
    }

    @And("водитель помечен как удаленный в базе данных")
    public void checkSoftDeleteInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.isDeleted()).isTrue();
    }

    @When("отправлен POST запрос на {string} с телом запроса:")
    public void sendPostRequestWithBodyForCar(String path, String body) {
        response = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post(path);
    }

    @And("автомобиль сохранен в базе данных")
    public void checkCarInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.getCars()).anyMatch(car -> car.getNumber().equals("A123BC"));
    }

    @And("данные автомобиля обновлены в базе данных")
    public void checkUpdatedCarInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.getCars()).anyMatch(car -> car.getBrand().equals("Honda") && car.getColor().equals("White"));
    }

    @And("автомобиль удален из базы данных")
    public void checkCarDeletedInDatabase() {
        Driver driver = driverRepository.findById("driver1").orElseThrow();
        assertThat(driver.getCars()).noneMatch(car -> car.getNumber().equals("A123BC"));
    }

    @Given("в системе существует {int} водителей")
    public void createDrivers(int amount) {
        for (int i = 1; i < amount; i++) {
            Driver driver = new Driver();
            driver.setId("driver" + i);
            driver.setName("Водитель " + i);
            driver.setExperience(i);
            driver.setPhone("+7999000000" + i);
            driver.setEmail("driver" + i + "@example.com");
            driver.setDeleted(false);
            driver.setStatus("FREE");
            driverRepository.save(driver);
        }
    }

    @When("отправлен GET запрос на {string}")
    public void sendGetRequest(String path) {
        response = given()
                .when()
                .get(path);
    }

    @And("в ответе содержится {int} водителей")
    public void checkDriverCount(int count) {
        DriverPageDTO driverPageDTO = response.as(DriverPageDTO.class);
        assertThat(driverPageDTO.getDrivers()).hasSize(count);
    }

    @And("поле {string} равно {int}")
    public void checkFieldValue(String field, int value) {
        DriverPageDTO driverPageDTO = response.as(DriverPageDTO.class);
        switch (field) {
            case "totalElements" -> assertThat(driverPageDTO.getTotalElements()).isEqualTo(value);
            case "totalPages" -> assertThat(driverPageDTO.getTotalPages()).isEqualTo(value);
        }
    }

    @And("поле {string} для машин равно {int}")
    public void checkFieldValueCar(String field, int value) {
        CarPageDTO carPageDTO = response.as(CarPageDTO.class);
        switch (field) {
            case "totalElements" -> assertThat(carPageDTO.getTotalElements()).isEqualTo(value);
            case "totalPages" -> assertThat(carPageDTO.getTotalPages()).isEqualTo(value);
        }
    }

    @Given("существует водитель с ID {string} и автомобиль с номером {string}")
    public void createDriverWithCar(String driverId, String carNumber) {
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        Car car = new Car();
        car.setNumber(carNumber);
        car.setBrand("Toyota");
        car.setColor("Black");
        driver.getCars().add(car);
        driverRepository.save(driver);
    }

    @Given("существует водитель с ID {string} и {int} автомобилей")
    public void createDriverWithCars(String driverId, int carCount) {
        Driver driver = driverRepository.findById(driverId).orElseThrow();
        for (int i = 1; i <= carCount; i++) {
            Car car = new Car();
            car.setNumber("A" + i + "BC");
            car.setBrand("Brand" + i);
            car.setColor("Color" + i);
            driver.getCars().add(car);
        }
        driverRepository.save(driver);
    }

    @And("в ответе содержится {int} автомобилей")
    public void checkCarCount(int count) {
        CarPageDTO carPageDTO = response.as(CarPageDTO.class);
        assertThat(carPageDTO.getCars()).hasSize(count);
    }
}