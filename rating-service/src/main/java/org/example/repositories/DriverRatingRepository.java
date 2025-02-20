package org.example.repositories;

import org.example.entities.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRatingRepository extends JpaRepository<DriverRating, Long>{
    @Modifying
    @Query("UPDATE DriverRating dr SET dr.deleted = true WHERE dr.driverId = :id")
    void softDelete(@Param("id") Long id);
    @Modifying
    @Query(value = "MERGE INTO driver_rating dr\n" +
            "USING (SELECT :driverId AS driverId, :rating AS rating) src\n" +
            "ON (dr.driver_id = src.driverId)\n" +
            "WHEN MATCHED THEN\n" +
            "    UPDATE SET \n" +
            "        average_rating = (dr.average_rating * dr.rating_count + src.rating) / (dr.rating_count + 1),\n" +
            "        rating_count = dr.rating_count + 1\n" +
            "WHEN NOT MATCHED THEN\n" +
            "    INSERT (driver_id, average_rating, rating_count)\n" +
            "    VALUES (src.driverId, src.rating, 1)", nativeQuery = true)
    void updateRating(@Param("driverId") Long id, @Param("rating") double rating);

    @Query("SELECT dr FROM DriverRating dr WHERE dr.deleted = false")
    List<DriverRating> findAllNotDeleted();

    @Query("SELECT dr FROM DriverRating dr WHERE dr.driverId = :driverId AND dr.deleted = false")
    Optional<DriverRating> findRating(@Param("driverId") Long id);
}
