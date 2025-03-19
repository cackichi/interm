package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.BalanceRepository;
import org.example.repositories.PaymentRepository;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PaymentServiceSteps {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    private PaymentDTO paymentDTO;
    private PaymentPageDTO paymentPageDTO;

    @Given("I have payment details")
    public void iHavePaymentDetails() {
        paymentDTO = new PaymentDTO();
        paymentDTO.setPassengerId(1L);
        paymentDTO.setRideId(100L);
        paymentDTO.setCost(50.0);
        paymentDTO.setStatus(Status.WAITING);
    }

    @When("I create the payment")
    public void iCreateThePayment() {
        paymentService.create(paymentDTO);
    }

    @Then("the payment should be created successfully")
    public void thePaymentShouldBeCreatedSuccessfully() {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment).isNotNull();
        assertThat(payment.getPassengerId()).isEqualTo(1L);
        assertThat(payment.getCost()).isEqualTo(50.0);
        assertThat(payment.getStatus()).isEqualTo(Status.WAITING);
    }

    @Given("I have a payment with passenger id {long} and cost {double}")
    public void iHaveAPaymentWithPassengerIdAndCost(Long passengerId, Double cost) {
        Payment payment = new Payment();
        payment.setPassengerId(passengerId);
        payment.setCost(cost);
        payment.setStatus(Status.WAITING);
        paymentRepository.save(payment);
    }

    @Given("I have a balance with passenger id {long} and balance {double}")
    public void iHaveABalanceWithPassengerIdAndBalance(Long passengerId, Double balance) {
        Balance balanceEntity = new Balance();
        balanceEntity.setPassengerId(passengerId);
        balanceEntity.setBalance(balance);
        balanceRepository.save(balanceEntity);
    }

    @When("I close the payment")
    public void iCloseThePayment() throws InsufficientBalanceException {
        paymentService.closePayment(1L);
    }

    @Then("the payment should be closed successfully")
    public void thePaymentShouldBeClosedSuccessfully() {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment.getStatus()).isEqualTo(Status.PAID);
    }

    @Given("I have a list of unpaid payments for passenger id {long}")
    public void iHaveAListOfUnpaidPaymentsForPassengerId(Long passengerId) {
        for (int i = 0; i < 10; i++) {
            Payment payment = new Payment();
            payment.setPassengerId(passengerId);
            payment.setCost(50.0 + i);
            payment.setStatus(Status.WAITING);
            paymentRepository.save(payment);
        }
    }

    @When("I get unpaid payments for passenger id {long}")
    public void iGetUnpaidPaymentsForPassengerId(Long passengerId) {
        Pageable pageable = PageRequest.of(0, 5);
        paymentPageDTO = paymentService.getUnpaid(passengerId, pageable);
    }

    @Then("I should get a paginated list of unpaid payments")
    public void iShouldGetAPaginatedListOfUnpaidPayments() {
        assertThat(paymentPageDTO.getPayments()).hasSize(5);
        assertThat(paymentPageDTO.getTotalPages()).isEqualTo(3);
        assertThat(paymentPageDTO.getTotalElements()).isEqualTo(11);
    }

    @Given("I have a list of paid payments for passenger id {long}")
    public void iHaveAListOfPaidPaymentsForPassengerId(Long passengerId) {
        for (int i = 0; i < 10; i++) {
            Payment payment = new Payment();
            payment.setPassengerId(passengerId);
            payment.setCost(50.0 + i);
            payment.setStatus(Status.PAID);
            paymentRepository.save(payment);
        }
    }

    @When("I get paid payments for passenger id {long}")
    public void iGetPaidPaymentsForPassengerId(Long passengerId) {
        Pageable pageable = PageRequest.of(0, 5);
        paymentPageDTO = paymentService.getPaid(passengerId, pageable);
    }

    @Then("I should get a paginated list of paid payments")
    public void iShouldGetAPaginatedListOfPaidPayments() {
        assertThat(paymentPageDTO.getPayments()).hasSize(5);
        assertThat(paymentPageDTO.getTotalPages()).isEqualTo(3);
        assertThat(paymentPageDTO.getTotalElements()).isEqualTo(11);
    }

    @Given("I have a payment with passenger id {long}")
    public void iHaveAPaymentWithPassengerId(Long passengerId) {
        Payment payment = new Payment();
        payment.setPassengerId(passengerId);
        payment.setCost(50.0);
        payment.setStatus(Status.WAITING);
        paymentRepository.save(payment);
    }

    @When("I soft delete payments for passenger id {long}")
    public void iSoftDeletePaymentsForPassengerId(Long passengerId) {
        paymentService.softDelete(passengerId);
    }

    @Then("the payments should be soft deleted")
    public void thePaymentsShouldBeSoftDeleted() {
        Payment payment = paymentRepository.findById(100L).orElseThrow();
        assertThat(payment.isDeleted()).isTrue();
    }

    @When("I hard delete payments for passenger id {long}")
    public void iHardDeletePaymentsForPassengerId(Long passengerId) {
        paymentRepository.deleteById(passengerId);
    }

    @Then("the payments should be hard deleted")
    public void thePaymentsShouldBeHardDeleted() {
        assertThat(paymentRepository.findById(1L).isEmpty()).isTrue();
    }
}