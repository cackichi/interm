package org.example.repositories;

import jakarta.transaction.Transactional;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRepo extends JpaRepository<Passenger, Long> {
    @Modifying
    @Query("UPDATE Passenger p SET p.deleted=true where p.id=:id")
    void softDelete(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Passenger p " +
            "SET p.name = CASE WHEN :name IS NOT NULL THEN :name ELSE p.name END, " +
            "    p.email = CASE WHEN :email IS NOT NULL THEN :email ELSE p.email END, " +
            "    p.phoneNumber = CASE WHEN :phoneNumber IS NOT NULL THEN :phoneNumber ELSE p.phoneNumber END " +
            "WHERE p.id = :id")
    void editData(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber
    );
    @Query("SELECT p FROM Passenger p WHERE p.deleted=false")
    List<Passenger> findAllNotDeleted();

    @Modifying
    @Query("UPDATE Passenger p SET p.status = :newStatus WHERE p.id = :id AND p.status = :currentStatus")
    int updateStatus(@Param("newStatus") Status newStatus,@Param("id") Long id, @Param("currentStatus") Status currentStatus);

    @Modifying
    @Query("UPDATE Passenger p SET p.status = :newStatus WHERE p.id = :passengerId")
    @Transactional
    void updateBecauseOfTravel(@Param("newStatus") Status newStatus, @Param("passengerId") Long passengerId);
}
