package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.exceptions.BusyDriverException;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.example.services.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class DriverServiceSteps {
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    private DriverDTO driverDTO;
    private DriverPageDTO driverPageDTO;

    @Given("I have driver details")
    public void iHaveDriverDetails() {
        driverRepository.deleteAll();
        driverDTO = new DriverDTO();
        driverDTO.setId("driver-123");
        driverDTO.setName("John Doe");
        driverDTO.setStatus("FREE");
    }

    @When("I create the driver")
    public void iCreateTheDriver() {
        driverService.create(driverDTO);
    }

    @Then("the driver should be created successfully")
    public void theDriverShouldBeCreatedSuccessfully() throws NotFoundException {
        DriverDTO createdDriver = driverService.findById(driverDTO.getId());
        assertThat(createdDriver).isNotNull();
        assertThat(createdDriver.getName()).isEqualTo(driverDTO.getName());
    }

    @Given("I have a driver with id {string}")
    public void iHaveADriverWithId(String id) {
        driverDTO = new DriverDTO();
        driverDTO.setId(id);
    }

    @When("I soft delete the driver")
    public void iSoftDeleteTheDriver() {
        driverService.softDelete(driverDTO.getId());
    }

    @Then("the driver should be soft deleted")
    public void theDriverShouldBeSoftDeleted() throws NotFoundException {
        assertThat(driverService.findById(driverDTO.getId()).isDeleted()).isTrue();
    }

    @When("I update the driver data")
    public void iUpdateTheDriverData() throws NotFoundException {
        driverDTO.setName("Gabriel");
        driverService.update(driverDTO.getId(), driverDTO);
    }

    @Then("the driver data should be updated")
    public void theDriverDataShouldBeUpdated() throws NotFoundException {
        DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
        assertThat(updatedDriver.getName()).isEqualTo(driverDTO.getName());
        assertThat(updatedDriver.getEmail()).isEqualTo(driverDTO.getEmail());
    }

    @When("I hard delete the driver")
    public void iHardDeleteTheDriver() {
        driverService.hardDelete(driverDTO.getId());
    }

    @Then("the driver should be hard deleted")
    public void theDriverShouldBeHardDeleted() {
        assertThrows(NotFoundException.class, () -> driverService.findById(driverDTO.getId()));
    }

    @Given("I have a list of not deleted drivers")
    public void iHaveAListOfNotDeletedDrivers() {
        for (int i = 0; i < 10; i++) {
            DriverDTO driver = new DriverDTO();
            driver.setId("driver-" + i);
            driver.setName("Driver " + i);
            driver.setStatus("FREE");
            driverService.create(driver);
            System.out.println(i);
        }
    }

    @When("I find all not deleted drivers")
    public void iFindAllNotDeletedDrivers() {
        Pageable pageable = PageRequest.of(0, 5);
        driverPageDTO = driverService.findAllNotDeleted(pageable);
    }

    @Then("I should get a paginated list of drivers")
    public void iShouldGetAPaginatedListOfDrivers() {
        assertThat(driverPageDTO.getDrivers()).hasSize(5);
        assertThat(driverPageDTO.getTotalPages()).isEqualTo(3);
        assertThat(driverPageDTO.getTotalElements()).isEqualTo(11);
    }

    @When("I find the driver by id")
    public void iFindTheDriverById() throws NotFoundException {
        driverDTO = driverService.findById(driverDTO.getId());
    }

    @Then("I should get the driver details")
    public void iShouldGetTheDriverDetails() {
        assertThat(driverDTO).isNotNull();
        assertThat(driverDTO.getName()).isEqualTo("Gabriel");
    }

    @Given("I have a driver with id {string} and status {string}")
    public void iHaveADriverWithIdAndStatus(String id, String status) {
        driverDTO = new DriverDTO();
        driverDTO.setId(id);
        driverDTO.setName("John Doe");
        driverDTO.setStatus(status);
    }

    @When("I update the driver status to {string}")
    public void iUpdateTheDriverStatusTo(String status) throws NotFoundException, BusyDriverException {
        driverService.updateStatusForTravel("driver-123", status);
    }

    @Then("the driver status should be updated to {string}")
    public void theDriverStatusShouldBeUpdatedTo(String status) throws NotFoundException {
        DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
        assertThat(updatedDriver.getStatus()).isEqualTo(status);
    }
}