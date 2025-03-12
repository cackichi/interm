package org.example.unit.services;

import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.example.services.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CarServiceImpl carService;
    private Car car;
    private CarDTO carDTO;
    private Driver driver;

    @BeforeEach
    void setUp(){
        driver = new Driver(
                "1",
                "Radrigo",
                3,
                "80445345698",
                "example@gmail.com",
                false,
                "FREE",
                new ArrayList<>()
        );
        car = new Car(
                "7846",
                "AUDI",
                "white",
                false
        );
        carDTO = new CarDTO(
                "7846",
                "AUDI",
                "white",
                false
        );
        driver.setCars(List.of(car));
    }

    @Test
    void mapToDTO(){
        when(modelMapper.map(car, CarDTO.class))
                .thenReturn(carDTO);

        CarDTO res = carService.mapToDTO(car);
        assertThat(res.getColor())
                .isEqualTo(carDTO.getColor());
        verify(modelMapper).map(car, CarDTO.class);
    }
    @Test
    void mapToCar(){
        when(modelMapper.map(carDTO, Car.class))
                .thenReturn(car);

        Car res = carService.mapToCar(carDTO);
        assertThat(res.getBrand()).isEqualTo(car.getBrand());
        verify(modelMapper).map(carDTO, Car.class);
    }
    @Test
    void removeCarFromDriver(){
        Query expectedQuery = Query.query(Criteria.where("_id").is(driver.getId()));
        Update expectedUpdate = new Update().pull("cars",
                Query.query(Criteria.where("number").is(car.getNumber())));

        carService.removeCarFromDriver(driver.getId(), car.getNumber());

        verify(mongoTemplate).updateFirst(expectedQuery, expectedUpdate, Driver.class);
    }
    @Test
    void updateCar(){
        Query expectedQuery = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(driver.getId()),
                Criteria.where("cars.number").is(carDTO.getNumber())
        ));
        Update expectedUpdate = new Update().set("cars.$.brand", "AUDI");
        expectedUpdate.set("cars.$.color", "white");

        carService.updateCar(driver.getId(), carDTO);

        verify(mongoTemplate).updateFirst(expectedQuery, expectedUpdate, Driver.class);
    }
    @Test
    void findCars(){
        when(driverRepository.findCarsById("1")).thenReturn(driver);

        CarPageDTO carPageDTO = carService.findCars("1", PageRequest.of(0,10));
        assertThat(carPageDTO.getTotalPages()).isEqualTo(1);
        assertThat(carPageDTO.getTotalElements()).isEqualTo(1);
    }
    @Test
    void findCar() throws NotFoundException {
        when(driverRepository.findById("1"))
                .thenReturn(Optional.of(driver));
        when(modelMapper.map(car, CarDTO.class))
                .thenReturn(carDTO);

        CarDTO result = carService.findCar("1", "7846");
        assertThat(result)
                .isNotNull();
        assertThat(result.getColor())
                .isEqualTo(carDTO.getColor());
        verify(modelMapper).map(car, CarDTO.class);
    }
    @Test
    void create(){
        when(modelMapper.map(carDTO, Car.class))
                .thenReturn(car);
        Query expectedQuery = Query.query(
                new Criteria().andOperator(
                        Criteria.where("_id").is(driver.getId())
                )
        );
        Update expectedUpdate = new Update()
                .addToSet("cars", car);

        carService.create(driver.getId(), carDTO);

        verify(modelMapper).map(carDTO, Car.class);
        verify(mongoTemplate).updateFirst(expectedQuery, expectedUpdate, Driver.class);
    }
}
