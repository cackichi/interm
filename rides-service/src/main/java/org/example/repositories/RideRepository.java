package org.example.repositories;

import org.example.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    @Modifying
    @Query("UPDATE Ride r SET r.deleted = true WHERE r.id = :id")
    void softDelete(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Ride r SET " +
            "r.pointA = COALESCE(:pointA, r.pointA), " +
            "r.pointB = COALESCE(:pointB, r.pointB) " +
            "WHERE r.id = :id")
    void update(@Param("id") Long id, @Param("pointA") String pointA, @Param("pointB") String pointB);

    @Query("SELECT r FROM Ride r WHERE r.deleted = false")
    List<Ride> findAllNotDeleted();

    @Modifying
    @Query("UPDATE Ride r SET r.status = :status WHERE r.id = :id")
    void updateStatus(Long id,String status);

    @Query(value = "SELECT id FROM ride r WHERE r.status = 'WAITING' LIMIT 1", nativeQuery = true)
    Long getOneWait();
}
