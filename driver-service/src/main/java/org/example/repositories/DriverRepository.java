package org.example.repositories;

import org.example.collections.Car;
import org.example.collections.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {
    @Query("{ _id: ?0 }")
    @Update("{ $set: { deleted: true }}")
    void softDelete(String id);

    @Query("{ _id: ?0}")
    @Update("{'$set': {\n" +
            "    'name': ?1,\n" +
            "    'experience': ?2,\n" +
            "    'phone': ?3,\n" +
            "    'email': ?4\n" +
            "}}")
    void update(String id, String name, int experience, String phone, String email);

    @Query("{deleted: false}")
    Page<Driver> findAllNotDeleted(Pageable pageable);

    @Query("{ _id: ?0 }")
    @Update("{ $set: { status: ?1 }}")
    void updateStatus(String id, String status);

    @Query(value = "{ _id: ?1 , $pull: { cars: { number: ?0 } } }", fields = "cars")
    void removeCarFromDriver(String brand, String driverId);

    @Query(value = "{ _id: ?0, 'cars' : { '$exists' : true } }")
    Driver findCarsById(String driverId);

    @Query(value = "{ _id: ?0 , 'cars.number' : ?1 }",
            fields = "{'cars': 1}")
    Optional<Car> findCarByNumber(String id, String number);
}
