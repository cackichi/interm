package org.example.repositories;

import org.example.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.status = 'WAITING' AND p.passengerId = :passengerId")
    List<Payment> getUnpaid(@Param("passengerId") Long passengerId);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PAID' AND p.passengerId = :passengerId AND p.deleted = false")
    List<Payment> getPaid(@Param("passengerId") Long passengerId);

    @Modifying
    @Query("UPDATE Payment p SET p.deleted = true WHERE p.passengerId = :passengerId")
    void softDelete(@Param("passengerId") Long passengerId);

    void deleteByPassengerId(Long passengerId);

    @Modifying
    @Query(value = """
             INSERT INTO Payment (passenger_id, ride_id, cost)
               SELECT v.*
               FROM (VALUES (:passengerId, :rideId, :cost)) AS v (passenger_id, ride_id, cost)
               WHERE NOT EXISTS (
                   SELECT 1
                   FROM Payment p
                   WHERE p.status = 'WAITING'
               );
            """, nativeQuery = true)
    int createIfNoPaidPayments(
            @Param("passengerId") Long passengerId,
            @Param("rideId") Long rideId,
            @Param("cost") Double cost
    );
}
