package org.example.endtoend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.entities.DriverRating;
import org.example.entities.PassengerRating;
import org.example.repositories.DriverRatingRepository;
import org.example.repositories.PassengerRatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndSteps {

    private Response response;

    @LocalServerPort
    private int port;

    @Autowired
    private DriverRatingRepository driverRatingRepository;

    @Autowired
    private PassengerRatingRepository passengerRatingRepository;

    @Given("отправлен POST запрос на {string} с телом:")
    public void sendPostRequest(String path, String body) {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        response = given()
                .contentType("application/json")
                .body(body)
                .post(path);
    }

    @Then("статус ответа должен быть {int}")
    public void checkStatus(int status) {
        assertThat(response.getStatusCode()).isEqualTo(status);
    }

    @And("рейтинг водителя сохранен в базе данных")
    public void checkDriverRatingSaved() {
        DriverRating rating = driverRatingRepository.findById("driver1").orElseThrow();
        assertThat(rating.getAverageRating()).isEqualTo(4.5);
        assertThat(rating.isDeleted()).isFalse();
    }

    @Given("существует рейтинг для водителя с ID {string}")
    public void createDriverRating(String driverId) {
        DriverRating rating = new DriverRating();
        rating.setDriverId(driverId);
        rating.setAverageRating(4.5);
        rating.setRatingCount(0);
        rating.setDeleted(false);
        driverRatingRepository.save(rating);
    }

    @When("отправлен GET запрос на {string}")
    public void sendGetRequest(String path) {
        response = given().get(path);
    }

    @And("в ответе содержится рейтинг {string}")
    public void checkRatingResponse(String rating) {
        assertThat(response.getBody().asString()).contains(rating);
    }

    @When("отправлен PATCH запрос на {string} с параметром rating={string}")
    public void sendPatchRequest(String path, String rating) {
        response = given()
                .queryParam("rating", rating)
                .patch(path);
    }

    @And("рейтинг водителя обновлен до {string}")
    public void checkDriverRatingUpdated(String rating) {
        DriverRating entity = driverRatingRepository.findById("driver1").orElseThrow();
        assertThat(entity.getAverageRating()).isEqualTo(Double.parseDouble(rating));
    }

    @When("отправлен DELETE запрос на {string}")
    public void sendDeleteRequest(String path) {
        response = given().delete(path);
    }

    @And("рейтинг водителя помечен как удаленный в базе данных")
    public void checkDriverSoftDelete() {
        DriverRating rating = driverRatingRepository.findById("driver1").orElseThrow();
        assertThat(rating.isDeleted()).isTrue();
    }

    @And("рейтинг пассажира сохранен в базе данных")
    public void checkPassengerRatingSaved() {
        PassengerRating rating = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(rating.getAverageRating()).isEqualTo(4.5);
        assertThat(rating.isDeleted()).isFalse();
    }

    @Given("существует рейтинг для пассажира с ID {long}")
    public void createPassengerRating(long id) {
        PassengerRating rating = new PassengerRating();
        rating.setPassengerId(id);
        rating.setAverageRating(4.5);
        rating.setRatingCount(0);
        rating.setDeleted(false);
        passengerRatingRepository.save(rating);
    }

    @And("рейтинг пассажира обновлен до {string}")
    public void checkPassengerRatingUpdated(String rating) {
        PassengerRating entity = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(entity.getAverageRating()).isEqualTo(Double.parseDouble(rating));
    }

    @And("рейтинг пассажира помечен как удаленный в базе данных")
    public void checkPassengerSoftDelete() {
        PassengerRating rating = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(rating.isDeleted()).isTrue();
    }
}