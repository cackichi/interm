package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PassengerRatingServiceSteps {
    @Autowired
    private PassengerRatingService passengerRatingService;
    @Autowired
    private PassengerRatingRepository passengerRatingRepository;

    private PassengerRatingDTO passengerRatingDTO;
    private List<PassengerRating> passengerRatings;
    private double rating;

    @Given("I have passenger rating details")
    public void iHavePassengerRatingDetails() {
        passengerRatingDTO = new PassengerRatingDTO();
        passengerRatingDTO.setPassengerId(1L);
        passengerRatingDTO.setAverageRating(4.5);
    }

    @When("I create the passenger rating")
    public void iCreateThePassengerRating() {
        passengerRatingService.create(passengerRatingDTO);
    }

    @Then("the passenger rating should be created successfully")
    public void thePassengerRatingShouldBeCreatedSuccessfully() {
        PassengerRating passengerRating = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(passengerRating).isNotNull();
        assertThat(passengerRating.getPassengerId()).isEqualTo(1L);
        assertThat(passengerRating.getAverageRating()).isEqualTo(4.5);
    }

    @Given("I have a passenger rating with id {long}")
    public void iHaveAPassengerRatingWithId(Long id) {
        PassengerRating passengerRating = new PassengerRating();
        passengerRating.setPassengerId(id);
        passengerRating.setAverageRating(4.0);
        passengerRatingRepository.save(passengerRating);
    }

    @When("I update or save the passenger rating with rating {double}")
    public void iUpdateOrSaveThePassengerRatingWithRating(Double rating) {
        passengerRatingService.updateOrSaveRating(1L, rating);
    }

    @Then("the passenger rating should be updated or saved successfully")
    public void thePassengerRatingShouldBeUpdatedOrSavedSuccessfully() {
        PassengerRating passengerRating = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(passengerRating.getAverageRating()).isEqualTo(4.5);
    }

    @Given("I have a passenger rating with id {long} and rating {double}")
    public void iHaveAPassengerRatingWithIdAndRating(Long id, Double rating) {
        PassengerRating passengerRating = new PassengerRating();
        passengerRating.setPassengerId(id);
        passengerRating.setAverageRating(rating);
        passengerRatingRepository.save(passengerRating);
    }

    @When("I find the passenger rating")
    public void iFindThePassengerRating() {
        rating = passengerRatingService.findRating(1L);
    }

    @Then("I should get the passenger rating details")
    public void iShouldGetThePassengerRatingDetails() {
        assertThat(rating).isEqualTo(4.5);
    }

    @Given("I have a list of not deleted passenger ratings")
    public void iHaveAListOfNotDeletedPassengerRatings() {
        for (int i = 2; i < 12; i++) {
            PassengerRating passengerRating = new PassengerRating();
            passengerRating.setPassengerId((long) i);
            passengerRating.setAverageRating(4.0 + i * 0.1);
            passengerRatingRepository.save(passengerRating);
        }
    }

    @When("I find all not deleted passenger ratings")
    public void iFindAllNotDeletedPassengerRatings() {
        passengerRatings = passengerRatingService.findAllNotDeleted();
    }

    @Then("I should get a list of not deleted passenger ratings")
    public void iShouldGetAListOfNotDeletedPassengerRatings() {
        assertThat(passengerRatings).hasSize(11);
    }

    @When("I soft delete the passenger rating")
    public void iSoftDeleteThePassengerRating() {
        passengerRatingService.softDelete(1L);
    }

    @Then("the passenger rating should be soft deleted")
    public void thePassengerRatingShouldBeSoftDeleted() {
        PassengerRating passengerRating = passengerRatingRepository.findById(1L).orElseThrow();
        assertThat(passengerRating.isDeleted()).isTrue();
    }

    @When("I hard delete the passenger rating")
    public void iHardDeleteThePassengerRating() {
        passengerRatingService.hardDelete(1L);
    }

    @Then("the passenger rating should be hard deleted")
    public void thePassengerRatingShouldBeHardDeleted() {
        assertThat(passengerRatingRepository.findById(1L).isEmpty()).isTrue();
    }
}
