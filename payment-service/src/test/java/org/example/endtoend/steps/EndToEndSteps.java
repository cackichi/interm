package org.example.endtoend.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.dto.BalanceDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.repositories.BalanceRepository;
import org.example.repositories.PaymentRepository;
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
    private BalanceRepository balanceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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

    @And("баланс сохранен в базе данных")
    public void checkBalanceInDatabase() {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(100.0);
    }

    @Given("существует баланс для пассажира с ID {long}")
    public void createBalance(long passengerId) {
        Balance balance = new Balance();
        balance.setPassengerId(passengerId);
        balance.setBalance(100.0);
        balance.setDeleted(false);
        balanceRepository.save(balance);
    }

    @When("отправлен GET запрос на {string}")
    public void sendGetRequest(String path) {
        response = given()
                .when()
                .get(path);
    }

    @And("в ответе содержится баланс с passengerId {long}")
    public void checkBalanceResponse(long passengerId) {
        BalanceDTO balance = response.as(BalanceDTO.class);
        assertThat(balance.getPassengerId()).isEqualTo(passengerId);
    }

    @When("отправлен PATCH запрос на {string} с параметром deposit={string}")
    public void sendPatchRequestWithDeposit(String path, String deposit) {
        response = given()
                .param("deposit", deposit)
                .when()
                .patch(path);
    }

    @And("баланс пассажира увеличен на {string}")
    public void checkBalanceIncreased(String deposit) {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(100.0 + Double.parseDouble(deposit));
    }

    @When("отправлен DELETE запрос на {string}")
    public void sendDeleteRequest(String path) {
        response = given()
                .when()
                .delete(path);
    }

    @And("баланс помечен как удаленный в базе данных")
    public void checkBalanceSoftDelete() {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.isDeleted()).isTrue();
    }

    @And("платеж сохранен в базе данных")
    public void checkPaymentInDatabase() {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment.getCost()).isEqualTo(50.0);
    }

    @Given("существует платеж для пассажира с ID {long} и статусом {string}")
    public void createPaymentWithStatus(long passengerId, String status) {
        Payment payment = new Payment();
        payment.setPassengerId(passengerId);
        payment.setRideId(100L);
        payment.setCost(50.0);
        payment.setStatus(Status.valueOf(status));
        payment.setDeleted(false);
        paymentRepository.save(payment);
    }

    @Given("существует платеж для пассажира с ID {long}")
    public void createPayment(long passengerId) {
        Payment payment = new Payment();
        payment.setPassengerId(passengerId);
        payment.setRideId(100L);
        payment.setCost(50.0);
        payment.setStatus(Status.WAITING);
        payment.setDeleted(false);
        paymentRepository.save(payment);
    }

    @And("существует баланс для пассажира с ID {long} и балансом {string}")
    public void createBalanceWithAmount(long passengerId, String balanceAmount) {
        Balance balance = new Balance();
        balance.setPassengerId(passengerId);
        balance.setBalance(Double.parseDouble(balanceAmount));
        balance.setDeleted(false);
        balanceRepository.save(balance);
    }

    @When("отправлен PATCH запрос на {string}")
    public void sendPatchRequest(String path) {
        response = given()
                .when()
                .patch(path);
    }

    @And("статус платежа изменен на {string}")
    public void checkPaymentStatus(String status) {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(Status.valueOf(status));
    }

    @And("баланс пассажира уменьшен на стоимость платежа")
    public void checkBalanceDecreased() {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(50.0); // 100.0 - 50.0
    }

    @Given("существует {int} незакрытых платежей для пассажира с ID {long}")
    public void createUnpaidPayments(int count, long passengerId) {
        for (int i = 1; i < count; i++) {
            Payment payment = new Payment();
            payment.setPassengerId(passengerId);
            payment.setRideId(100L + i);
            payment.setCost(50.0);
            payment.setStatus(Status.WAITING);
            payment.setDeleted(false);
            paymentRepository.save(payment);
        }
    }

    @And("в ответе содержится {int} платежей")
    public void checkPaymentCount(int count) {
        PaymentPageDTO paymentPageDTO = response.as(PaymentPageDTO.class);
        assertThat(paymentPageDTO.getPayments()).hasSize(count);
    }

    @And("поле {string} равно {int}")
    public void checkFieldValue(String field, int value) {
        PaymentPageDTO paymentPageDTO = response.as(PaymentPageDTO.class);
        switch (field) {
            case "totalElements" -> assertThat(paymentPageDTO.getTotalElements()).isEqualTo(value);
            case "totalPages" -> assertThat(paymentPageDTO.getTotalPages()).isEqualTo(value);
        }
    }

    @Given("существует {int} закрытых платежей для пассажира с ID {long}")
    public void createPaidPayments(int count, long passengerId) {
        for (int i = 1; i < count; i++) {
            Payment payment = new Payment();
            payment.setPassengerId(passengerId);
            payment.setRideId(100L + i);
            payment.setCost(50.0);
            payment.setStatus(Status.PAID);
            payment.setDeleted(false);
            paymentRepository.save(payment);
        }
    }

    @And("платеж помечен как удаленный в базе данных")
    public void checkPaymentSoftDelete() {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment.isDeleted()).isTrue();
    }
}