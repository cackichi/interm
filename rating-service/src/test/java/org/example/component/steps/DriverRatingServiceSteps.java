package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;
import org.example.repositories.DriverRatingRepository;
import org.example.services.DriverRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DriverRatingServiceSteps {
    @Autowired
    private DriverRatingService driverRatingService;
    @Autowired
    private DriverRatingRepository driverRatingRepository;

    private DriverRatingDTO driverRatingDTO;
    private List<DriverRating> driverRatings;
    private double rating;

    @Given("I have driver rating details")
    public void iHaveDriverRatingDetails() {
        driverRatingDTO = new DriverRatingDTO();
        driverRatingDTO.setDriverId("driver-123");
        driverRatingDTO.setAverageRating(4.5);
    }

    @When("I create the driver rating")
    public void iCreateTheDriverRating() {
        driverRatingService.create(driverRatingDTO);
    }

    @Then("the driver rating should be created successfully")
    public void theDriverRatingShouldBeCreatedSuccessfully() {
        DriverRating driverRating = driverRatingRepository.findById("driver-123").orElseThrow();
        assertThat(driverRating).isNotNull();
        assertThat(driverRating.getDriverId()).isEqualTo("driver-123");
        assertThat(driverRating.getAverageRating()).isEqualTo(4.5);
    }

    @Given("I have a driver rating with id {string}")
    public void iHaveADriverRatingWithId(String id) {
        DriverRating driverRating = new DriverRating();
        driverRating.setDriverId(id);
        driverRating.setAverageRating(4.0);
        driverRatingRepository.save(driverRating);
    }

    @When("I update or save the driver rating with rating {double}")
    public void iUpdateOrSaveTheDriverRatingWithRating(Double rating) {
        driverRatingService.updateOrSaveRating("driver-123", rating);
    }

    @Then("the driver rating should be updated or saved successfully")
    public void theDriverRatingShouldBeUpdatedOrSavedSuccessfully() {
        DriverRating driverRating = driverRatingRepository.findById("driver-123").orElseThrow();
        assertThat(driverRating.getAverageRating()).isEqualTo(4.5);
    }

    @Given("I have a driver rating with id {string} and rating {double}")
    public void iHaveADriverRatingWithIdAndRating(String id, Double rating) {
        DriverRating driverRating = new DriverRating();
        driverRating.setDriverId(id);
        driverRating.setAverageRating(rating);
        driverRatingRepository.save(driverRating);
    }

    @When("I find the driver rating")
    public void iFindTheDriverRating() {
        rating = driverRatingService.findRating("driver-123");
    }

    @Then("I should get the driver rating details")
    public void iShouldGetTheDriverRatingDetails() {
        assertThat(rating).isEqualTo(4.5);
    }

    @Given("I have a list of not deleted driver ratings")
    public void iHaveAListOfNotDeletedDriverRatings() {
        for (int i = 0; i < 10; i++) {
            DriverRating driverRating = new DriverRating();
            driverRating.setDriverId("driver-" + i);
            driverRating.setAverageRating(4.0 + i * 0.1);
            driverRatingRepository.save(driverRating);
        }
    }

    @When("I find all not deleted driver ratings")
    public void iFindAllNotDeletedDriverRatings() {
        driverRatings = driverRatingService.findAllNotDeleted();
    }

    @Then("I should get a list of not deleted driver ratings")
    public void iShouldGetAListOfNotDeletedDriverRatings() {
        assertThat(driverRatings).hasSize(11);
    }

    @When("I soft delete the driver rating")
    public void iSoftDeleteTheDriverRating() {
        driverRatingService.softDelete("driver-123");
    }

    @Then("the driver rating should be soft deleted")
    public void theDriverRatingShouldBeSoftDeleted() {
        DriverRating driverRating = driverRatingRepository.findById("driver-123").orElseThrow();
        assertThat(driverRating.isDeleted()).isTrue();
    }

    @When("I hard delete the driver rating")
    public void iHardDeleteTheDriverRating() {
        driverRatingService.hardDelete("driver-123");
    }

    @Then("the driver rating should be hard deleted")
    public void theDriverRatingShouldBeHardDeleted() {
        assertThat(driverRatingRepository.findById("driver-123").isEmpty()).isTrue();
    }
}