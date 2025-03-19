package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityNotFoundException;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.entities.Ride;
import org.example.entities.Status;
import org.example.exceptions.NoWaitingRideException;
import org.example.repositories.RideRepository;
import org.example.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RideServiceSteps {
    @Autowired
    private RideService rideService;
    @Autowired
    private RideRepository rideRepository;

    private RideDTO rideDTO;
    private RidePageDTO ridePageDTO;

    @Given("I have ride details")
    public void iHaveRideDetails() {
        rideDTO = new RideDTO();
        rideDTO.setPointA("Point A");
        rideDTO.setPointB("Point B");
        rideDTO.setPassengerId(1L);
        rideDTO.setStatus(Status.WAITING);
    }

    @When("I create the ride")
    public void iCreateTheRide() {
        rideDTO = rideService.create(rideDTO);
    }

    @Then("the ride should be created successfully")
    public void theRideShouldBeCreatedSuccessfully() {
        assertThat(rideDTO).isNotNull();
        assertThat(rideDTO.getPointA()).isEqualTo("Point A");
        assertThat(rideDTO.getPointB()).isEqualTo("Point B");
        assertThat(rideDTO.getStatus()).isEqualTo(Status.WAITING);
    }

    @Given("I have a ride with id {long}")
    public void iHaveARideWithId(Long id) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setPointA("Point A");
        ride.setPointB("Point B");
        ride.setPassengerId(1L);
        ride.setStatus(Status.WAITING);
        rideRepository.save(ride);
    }

    @When("I soft delete the ride")
    public void iSoftDeleteTheRide() {
        rideService.softDelete(100L);
    }

    @Then("the ride should be soft deleted")
    public void theRideShouldBeSoftDeleted() {
        Ride ride = rideRepository.findById(100L).orElseThrow();
        assertThat(ride.isDeleted()).isTrue();
    }

    @When("I update the ride details")
    public void iUpdateTheRideDetails() {
        rideDTO = new RideDTO();
        rideDTO.setPointA("New Point A");
        rideDTO.setPointB("New Point B");
        rideService.update(100L, rideDTO);
    }

    @Then("the ride details should be updated")
    public void theRideDetailsShouldBeUpdated() {
        RideDTO updatedRide = rideService.findById(100L);
        assertThat(updatedRide.getPointA()).isEqualTo("New Point A");
        assertThat(updatedRide.getPointB()).isEqualTo("New Point B");
    }

    @When("I hard delete the ride")
    public void iHardDeleteTheRide() {
        rideService.hardDelete(100L);
    }

    @Then("the ride should be hard deleted")
    public void theRideShouldBeHardDeleted() {
        assertThrows(EntityNotFoundException.class, () -> rideService.findById(100L));
    }

    @Given("I have a list of not deleted rides")
    public void iHaveAListOfNotDeletedRides() {
        for (int i = 0; i < 10; i++) {
            Ride ride = new Ride();
            ride.setId(100L + i);
            ride.setPointA("Point A" + i);
            ride.setPointB("Point B" + i);
            ride.setPassengerId(1L + i);
            ride.setStatus(Status.WAITING);
            rideRepository.save(ride);
        }
    }

    @When("I find all not deleted rides")
    public void iFindAllNotDeletedRides() {
        Pageable pageable = PageRequest.of(0, 5);
        ridePageDTO = rideService.findAllNotDeleted(pageable);
    }

    @Then("I should get a paginated list of rides")
    public void iShouldGetAPaginatedListOfRides() {
        assertThat(ridePageDTO.getRides()).hasSize(5);
        assertThat(ridePageDTO.getTotalPages()).isEqualTo(2);
        assertThat(ridePageDTO.getTotalElem()).isEqualTo(10);
    }

    @When("I find the ride by id")
    public void iFindTheRideById() {
        rideDTO = rideService.findById(100L);
    }

    @Then("I should get the ride details")
    public void iShouldGetTheRideDetails() {
        assertThat(rideDTO).isNotNull();
        assertThat(rideDTO.getPointA()).isEqualTo("Point A");
        assertThat(rideDTO.getPointB()).isEqualTo("Point B");
    }

    @Given("I have a waiting ride")
    public void iHaveAWaitingRide() {
        Ride ride = new Ride();
        ride.setId(100L);
        ride.setPointA("Point A");
        ride.setPointB("Point B");
        ride.setPassengerId(1L);
        ride.setStatus(Status.WAITING);
        rideRepository.save(ride);
    }

    @When("I update the ride status to {string}")
    public void iUpdateTheRideStatusTo(String status) {
        rideService.updateStatus(100L, Status.valueOf(status));
    }

    @Given("I have a ride with id {long} and driver {string}")
    public void iHaveARideWithIdAndDriver(Long id, String driverId) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setPointA("Point A");
        ride.setPointB("Point B");
        ride.setPassengerId(1L);
        ride.setDriverId(driverId);
        ride.setStatus(Status.TRAVELING);
        rideRepository.save(ride);
    }

    @Then("the ride status should be updated to {string}")
    public void theRideStatusShouldBeUpdatedToComplete(String status) {
        RideDTO updatedRide = rideService.findById(100L);
        assertThat(updatedRide.getStatus()).isEqualTo(Status.valueOf(status));
    }

    @When("I attach driver {string} to the ride")
    public void iAttachDriverToTheRide(String driverId) {
        rideService.attachDriver(driverId, 100L);
    }

    @Then("the driver should be attached to the ride")
    public void theDriverShouldBeAttachedToTheRide() {
        RideDTO updatedRide = rideService.findById(100L);
        assertThat(updatedRide.getDriverId()).isEqualTo("driver-123");
    }
}