package org.example.integration;

import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.example.services.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
@Testcontainers
public class CarServiceIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private CarService carService;

    @Autowired
    private DriverRepository driverRepository;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        driverRepository.deleteAll();

        testDriver = new Driver(
                "test-id",
                "Test Driver",
                3,
                "1234567890",
                "test@email.com",
                false,
                "FREE",
                new ArrayList<>()
        );
        driverRepository.save(testDriver);
    }

    @Test
    void testCreateCar() {
        CarDTO carDTO = new CarDTO("ABC123", "Toyota", "black", false);

        carService.create(testDriver.getId(), carDTO);

        Driver driver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assertThat(driver.getCars()).hasSize(1);
        Car savedCar = driver.getCars().get(0);
        assertThat(savedCar.getNumber()).isEqualTo(carDTO.getNumber());
        assertThat(savedCar.getBrand()).isEqualTo(carDTO.getBrand());
        assertThat(savedCar.getColor()).isEqualTo(carDTO.getColor());
    }

    @Test
    void testCreateCarWithExistingNumber() {
        CarDTO carDTO = new CarDTO("ABC123", "Toyota", "black", false);
        carService.create(testDriver.getId(), carDTO);

        CarDTO duplicateCar = new CarDTO("ABC123", "Honda", "red", false);
        carService.create(testDriver.getId(), duplicateCar);

        Driver driver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assertThat(driver.getCars()).hasSize(2);
        List<String> numbers = driver.getCars().stream()
                .map(Car::getNumber)
                .collect(Collectors.toList());
        assertThat(numbers).containsExactlyInAnyOrder("ABC123", "ABC123");
    }

    @Test
    void testUpdateCar() {
        CarDTO initialCar = new CarDTO("ABC123", "Toyota", "black", false);
        carService.create(testDriver.getId(), initialCar);

        CarDTO updatedCar = new CarDTO("ABC123", "Honda", "red", false);
        carService.updateCar(testDriver.getId(), updatedCar);

        Driver driver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assertThat(driver.getCars()).hasSize(1);
        Car savedCar = driver.getCars().get(0);
        assertThat(savedCar.getBrand()).isEqualTo(updatedCar.getBrand());
        assertThat(savedCar.getColor()).isEqualTo(updatedCar.getColor());
    }

    @Test
    void testRemoveCar() {
        CarDTO car = new CarDTO("ABC123", "Toyota", "black", false);
        carService.create(testDriver.getId(), car);

        carService.removeCarFromDriver(testDriver.getId(), "ABC123");

        Driver driver = driverRepository.findById(testDriver.getId()).orElseThrow();
        assertThat(driver.getCars()).isEmpty();
    }

    @Test
    void testFindCarsPaginated() {
        List<CarDTO> cars = List.of(
                new CarDTO("ABC123", "Toyota", "black", false),
                new CarDTO("DEF456", "Honda", "white", false),
                new CarDTO("GHI789", "Ford", "red", false)
        );

        for (CarDTO car : cars) {
            carService.create(testDriver.getId(), car);
        }

        Pageable pageable = PageRequest.of(0, 2);
        CarPageDTO page = carService.findCars(testDriver.getId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getCars()).hasSize(2);
    }

    @Test
    void testFindCarByIdAndNumber() throws NotFoundException {
        CarDTO car = new CarDTO("ABC123", "Toyota", "black", false);
        carService.create(testDriver.getId(), car);

        CarDTO foundCar = carService.findCar(testDriver.getId(), "ABC123");
        assertThat(foundCar.getNumber()).isEqualTo(car.getNumber());
        assertThat(foundCar.getBrand()).isEqualTo(car.getBrand());
        assertThat(foundCar.getColor()).isEqualTo(car.getColor());
    }

    @Test
    void testFindNonExistingCar() {
        assertThrows(NotFoundException.class, () -> carService.findCar(testDriver.getId(), "XYZ789"));
    }
}
