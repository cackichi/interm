package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import org.example.dto.BalanceDTO;
import org.example.entities.Balance;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BalanceServiceSteps {
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;

    private BalanceDTO balanceDTO;

    @Given("I have balance details")
    public void iHaveBalanceDetails() {
        balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(0.0);
    }

    @When("I create the balance")
    public void iCreateTheBalance() {
        balanceService.create(balanceDTO);
    }

    @Then("the balance should be created successfully")
    public void theBalanceShouldBeCreatedSuccessfully() {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance).isNotNull();
        assertThat(balance.getPassengerId()).isEqualTo(1L);
        assertThat(balance.getBalance()).isEqualTo(0.0);
    }

    @Given("I have a balance with passenger id {long}")
    public void iHaveABalanceWithPassengerId(Long passengerId) {
        Balance balance = new Balance();
        balance.setPassengerId(passengerId);
        balance.setBalance(0.0);
        balanceRepository.save(balance);
    }

    @When("I top up the balance with {double}")
    public void iTopUpTheBalanceWith(Double amount) {
        balanceService.topUpBalance(1L, amount);
    }

    @Then("the balance should be updated with {double}")
    public void theBalanceShouldBeUpdatedWith(Double amount) {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.getBalance()).isEqualTo(amount);
    }

    @When("I get the balance")
    public void iGetTheBalance() {
        balanceDTO = balanceService.getBalance(1L);
    }

    @Then("I should get the balance details")
    public void iShouldGetTheBalanceDetails() {
        assertThat(balanceDTO).isNotNull();
        assertThat(balanceDTO.getPassengerId()).isEqualTo(1L);
        assertThat(balanceDTO.getBalance()).isEqualTo(0.0);
    }

    @When("I soft delete the balance")
    public void iSoftDeleteTheBalance() {
        balanceService.softDelete(1L);
    }

    @Then("the balance should be soft deleted")
    public void theBalanceShouldBeSoftDeleted() {
        Balance balance = balanceRepository.findById(1L).orElseThrow();
        assertThat(balance.isDeleted()).isTrue();
    }

    @When("I hard delete the balance")
    public void iHardDeleteTheBalance() {
        balanceService.hardDelete(1L);
    }

    @Then("the balance should be hard deleted")
    public void theBalanceShouldBeHardDeleted() {
        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(1L));
    }
}