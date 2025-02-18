package org.example.migrations;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.repositories.CarRepository;
import org.example.repositories.DriverRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "driver-car-initializer", order = "1", author = "mongock")
public class DriverCarInitializerChange {

    @BeforeExecution
    public void beforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection("driver");
        mongoTemplate.createCollection("car");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("driver");
        mongoTemplate.dropCollection("car");
    }

    @Execution
    public void execution(CarRepository carRepository, DriverRepository driverRepository) {
        driverRepository.save(new Driver("1", "Nicolas", 3, "80293554989", "nicolas@gmail.com", false));
        carRepository.save(new Car("1","Mersedes", "Red", "1", false));
    }

    @RollbackExecution
    public void rollbackExecution(CarRepository carRepository, DriverRepository driverRepository) {
        carRepository.deleteAll();
        driverRepository.deleteAll();
    }
}