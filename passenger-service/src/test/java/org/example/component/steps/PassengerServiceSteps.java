package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
public class PassengerServiceSteps {
    @Autowired
    private PassengerService passengerService;
    private PassengerDTO passengerDTO;
    private Passenger result;
    private PassengerPageDTO passengerPageDTO;
    private boolean softDeleteResult;
    private boolean hardDeleteResult;
    private boolean updateResult;
    private boolean orderTaxiResult;

    @Given("I have passenger details")
    public void iHavePassengerDetails() {
        passengerDTO = new PassengerDTO(
                1L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
    }

    @When("I save the passenger")
    public void iSaveThePassenger() {
        result = passengerService.save(passengerDTO);
    }

    @Then("the passenger should be saved successfully")
    public void thePassengerShouldBeSavedSuccessfully() {
        assertThat(result.getName()).isEqualTo(passengerDTO.getName());
        assertThat(result.getId()).isNotEqualTo(passengerDTO.getId());
    }

    @Given("I have a passenger with id {long}")
    public void iHaveAPassengerWithId(Long id) {
        passengerDTO = new PassengerDTO(
                id,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        passengerService.save(passengerDTO);
    }

    @When("I soft delete the passenger")
    public void iSoftDeleteThePassenger() {
        passengerService.softDelete(passengerDTO.getId());
        softDeleteResult = true;
    }

    @Then("the passenger should be soft deleted")
    public void thePassengerShouldBeSoftDeleted() {
        assertThat(softDeleteResult).isTrue();
    }

    @When("I update the passenger data")
    public void iUpdateThePassengerData() {
        passengerDTO.setName("Новое Имя");
        passengerDTO.setEmail("new@example.com");
        passengerDTO.setPhoneNumber("+78888888888");
        passengerService.updatePass(passengerDTO.getId(), passengerDTO);
        updateResult = true;
    }

    @Then("the passenger data should be updated")
    public void thePassengerDataShouldBeUpdated() {
        assertThat(updateResult).isTrue();
        PassengerDTO updatedPassenger = passengerService.findOne(passengerDTO.getId());
        assertThat(updatedPassenger.getName()).isEqualTo("Новое Имя");
        assertThat(updatedPassenger.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedPassenger.getPhoneNumber()).isEqualTo("+78888888888");
    }

    @When("I hard delete the passenger")
    public void iHardDeleteThePassenger() {
        passengerService.hardDelete(passengerDTO.getId());
        hardDeleteResult = true;
    }

    @Then("the passenger should be hard deleted")
    public void thePassengerShouldBeHardDeleted() {
        assertThat(hardDeleteResult).isTrue();
    }

    @Given("I have a list of not deleted passengers")
    public void iHaveAListOfNotDeletedPassengers() {
        for (int i = 0; i < 10; i++) {
            PassengerDTO passenger = new PassengerDTO(
                    (long) i,
                    "Passenger " + i,
                    "passenger" + i + "@example.com",
                    "+7999999999" + i,
                    false,
                    Status.NOT_ACTIVE
            );
            passengerService.save(passenger);
        }
    }

    @When("I find all not deleted passengers")
    public void iFindAllNotDeletedPassengers() {
        Pageable pageable = PageRequest.of(0, 5);
        passengerPageDTO = passengerService.findAllNotDeleted(pageable);
    }

    @Then("I should get a paginated list of passengers")
    public void iShouldGetAPaginatedListOfPassengers() {
        assertThat(passengerPageDTO.getPassengers()).hasSize(5);
        assertThat(passengerPageDTO.getTotalPages()).isEqualTo(3);
        assertThat(passengerPageDTO.getTotalElements()).isEqualTo(11);
    }

    @When("I find the passenger by id")
    public void iFindThePassengerById() {
        result = passengerService.mapToPass(passengerService.findOne(passengerDTO.getId()));
    }

    @Then("I should get the passenger details")
    public void iShouldGetThePassengerDetails() {
        assertThat(result.getId()).isEqualTo(passengerDTO.getId());
        assertThat(result.getName()).isEqualTo(passengerDTO.getName());
        assertThat(result.getEmail()).isEqualTo(passengerDTO.getEmail());
        assertThat(result.getPhoneNumber()).isEqualTo(passengerDTO.getPhoneNumber());
    }
}