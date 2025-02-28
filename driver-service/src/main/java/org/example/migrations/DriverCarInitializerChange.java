package org.example.migrations;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;

import org.example.collections.Driver;
import org.example.repositories.DriverRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "driver-car-initializer", order = "1", author = "mongock")
public class DriverCarInitializerChange {

    @BeforeExecution
    public void beforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection("driver");
    }

    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("driver");
    }

    @Execution
    public void execution(DriverRepository driverRepository) {
        Driver driver = new Driver("Nicolas", 3, "80293554989", "nicolas@gmail.com");
        driver.addCar("7865" ,"Mercedes", "Red");
        driverRepository.save(driver);
    }

    @RollbackExecution
    public void rollbackExecution(DriverRepository driverRepository) {
        driverRepository.deleteAll();
    }
}