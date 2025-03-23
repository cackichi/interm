package org.example.component.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.collections.Driver;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.example.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CarServiceSteps {
    @Autowired
    private CarService carService;
    @Autowired
    private DriverRepository driverRepository;

    private CarDTO carDTO;
    private Driver driver;
    private CarPageDTO carPageDTO;

    @Given("I have a driver with id {string} and car details")
    public void iHaveADriverWithId(String driverId) {
        driver = new Driver();
        driver.setId(driverId);
        driverRepository.save(driver);
        carDTO = new CarDTO();
        carDTO.setNumber("ABC123");
        carDTO.setBrand("Toyota");
        carDTO.setColor("Red");
    }

    @When("I create the car for the driver")
    public void iCreateTheCarForTheDriver() {
        carService.create(driver.getId(), carDTO);
    }

    @Then("the car should be created successfully")
    public void theCarShouldBeCreatedSuccessfully() throws NotFoundException {
        CarDTO foundCar = carService.findCar(driver.getId(), carDTO.getNumber());
        assertThat(foundCar).isNotNull();
        assertThat(foundCar.getNumber()).isEqualTo(carDTO.getNumber());
    }

    @Given("I have a driver with id {string} and a car with number {string}")
    public void iHaveADriverWithIdAndACarWithNumber(String driverId, String carNumber) {
        driver = new Driver();
        driver.setId(driverId);
        carDTO = new CarDTO();
        carDTO.setNumber(carNumber);
        carDTO.setBrand("Toyota");
        carDTO.setColor("Red");
        carService.create(driverId, carDTO);
    }

    @When("I update the car details")
    public void iUpdateTheCarDetails() {
        carDTO.setBrand("Honda");
        carDTO.setColor("Blue");
        carService.updateCar(driver.getId(), carDTO);
    }

    @Then("the car details should be updated")
    public void theCarDetailsShouldBeUpdated() throws NotFoundException {
        CarDTO updatedCar = carService.findCar("driver-123", "ABC123");
        assertThat(updatedCar.getBrand()).isEqualTo("Honda");
        assertThat(updatedCar.getColor()).isEqualTo("Blue");
    }

    @When("I remove the car from the driver")
    public void iRemoveTheCarFromTheDriver() {
        carService.removeCarFromDriver(driver.getId(), carDTO.getNumber());
    }

    @Then("the car should be removed from the driver")
    public void theCarShouldBeRemovedFromTheDriver() {
        assertThrows(NotFoundException.class, () -> carService.findCar(driver.getId(), "ABC123"));
    }

    @Given("I have a driver with id {string} and a list of cars")
    public void iHaveADriverWithIdAndAListOfCars(String driverId) {
        for (int i = 0; i < 10; i++) {
            CarDTO car = new CarDTO();
            car.setNumber("CAR" + i);
            car.setBrand("Brand" + i);
            car.setColor("Color" + i);
            carService.create(driverId, car);
        }
    }

    @When("I find all cars for the driver")
    public void iFindAllCarsForTheDriver() {
        Pageable pageable = PageRequest.of(0, 5);
        carPageDTO = carService.findCars("driver-123", pageable);
    }

    @Then("I should get a paginated list of cars")
    public void iShouldGetAPaginatedListOfCars() {
        assertThat(carPageDTO.getCars()).hasSize(5);
        assertThat(carPageDTO.getTotalPages()).isEqualTo(3);
        assertThat(carPageDTO.getTotalElements()).isEqualTo(11);
    }

    @When("I find the car by number")
    public void iFindTheCarByNumber() throws NotFoundException {
        carDTO = carService.findCar(driver.getId(), carDTO.getNumber());
    }

    @Then("I should get the car details")
    public void iShouldGetTheCarDetails() {
        assertThat(carDTO).isNotNull();
        assertThat(carDTO.getNumber()).isEqualTo("ABC123");
        assertThat(carDTO.getBrand()).isEqualTo("Honda");
        assertThat(carDTO.getColor()).isEqualTo("Blue");
    }
}