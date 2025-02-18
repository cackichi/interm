package org.example.repositories;

import org.example.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepo extends JpaRepository<Passenger, Long> {
    @Modifying
    @Query("UPDATE Passenger p SET p.deleted=true where p.id=:id")
    void softDelete(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Passenger p SET p.name =:name, p.email=:email, p.phoneNumber=:phoneNumber WHERE p.id = :id")
    int editData(@Param("id") Long id, @Param("name") String name, @Param("email") String email, @Param("phoneNumber") String phoneNumber);
}
