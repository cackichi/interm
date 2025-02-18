package org.example.repositories;


import org.example.collections.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends MongoRepository<Car, String> {
    @Query("{ driverId: ?0 }")
    @Update("{ $set: { deleted: true }}")
    void softDelete(String driverId);

    @Query("{ _id: ?0}")
    @Update("{'$set': {\n" +
            "    'brand': ?1,\n" +
            "    'color': ?2\n" +
            "}}")
    void update(String id, String brand, String color);

    @Query("{deleted: false}")
    List<Car> findAllNotDeleted();
}