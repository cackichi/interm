package org.example.repositories;

import org.example.collections.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    List<Driver> findAllNotDeleted();
}
