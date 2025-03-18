package org.example.component.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PassengerAPISteps {
    private Response response;
    private PassengerDTO passengerDTO;

    @LocalServerPort
    private int port;

    @Given("that I have passenger details")
    public void detailsOfNewPassenger() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        this.passengerDTO = new PassengerDTO(
                100L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
    }

    @When("I send a POST request to endpoint {string}")
    public void createPassenger(String path) {
        response = given()
                .contentType("application/json")
                .body(passengerDTO)
                .when()
                .post(path);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCode(int statusCode) {
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
    }

    @And("the response body should contain the new passenger")
    public void theResponseBodyShouldContainTheNewPassenger() {
        PassengerDTO saved = response.as(PassengerDTO.class);
        assertThat(saved.getName()).isEqualTo(passengerDTO.getName());
    }

    @When("I send a PATCH request to endpoint {string} with id {long}")
    public void updatePassenger(String path, long id) {
        PassengerDTO updatedPassenger = new PassengerDTO(
                id,
                "Обновленный Иван",
                "updated@example.com",
                "+79998887766",
                true,
                Status.WAITING
        );
        response = given()
                .contentType("application/json")
                .body(updatedPassenger)
                .when()
                .patch(path + "/" + id);
    }

    @When("I send a GET request to endpoint {string} with page {int} and size {int}")
    public void findAllPassengers(String path, int page, int size) {
        response = given()
                .param("page", page)
                .param("size", size)
                .when()
                .get(path);
    }

    @And("the response body should contain a list of passengers")
    public void theResponseBodyShouldContainListOfPassengers() {
        PassengerPageDTO passengerPageDTO = response.as(PassengerPageDTO.class);
        assertThat(passengerPageDTO.getPassengers()).isNotEmpty();
    }

    @When("I send a GET request to endpoint {string} with id {long}")
    public void findOnePassenger(String path, long id) {
        response = given()
                .when()
                .get(path + "/" + id);
    }

    @And("the response body should contain the passenger with id {long}")
    public void theResponseBodyShouldContainPassenger(long id) {
        PassengerDTO passenger = response.as(PassengerDTO.class);
        assertThat(passenger.getName()).isEqualTo("Обновленный Иван");
    }

    @When("I send a PATCH request to endpoint {string} with passenger id {int}, pointA {string} and pointB {string}")
    public void orderTaxi(String path, long passengerId, String pointA, String pointB) {
        response = given()
                .queryParam("pointA", pointA)
                .queryParam("pointB", pointB)
                .when()
                .patch(path + "/" + passengerId);
    }

    @And("the response body should contain a message {string}")
    public void theResponseBodyShouldContainMessage(String message) {
        String responseMessage = response.getBody().asString();
        assertThat(responseMessage).contains(message);
    }

    @When("I send a DELETE request to endpoint {string} with id {long}")
    public void softDeletePassenger(String path, long id) {
        response = given()
                .when()
                .delete(path + "/" + id);
    }
}