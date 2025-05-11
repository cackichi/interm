package org.example.repositories;

import org.example.entities.PassengerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassengerRatingRepository extends JpaRepository<PassengerRating, Long> {
    @Modifying
    @Query("UPDATE PassengerRating pr SET pr.deleted = true WHERE pr.passengerId = :id")
    void softDelete(@Param("id") Long id);
    @Modifying
    @Query(value = "MERGE INTO passenger_rating pr\n" +
            "USING (SELECT :passengerId AS passengerId, :rating AS rating) src\n" +
            "ON (pr.passenger_id = src.passengerId)\n" +
            "WHEN MATCHED THEN\n" +
            "    UPDATE SET \n" +
            "        average_rating = (pr.average_rating * pr.rating_count + src.rating) / (pr.rating_count + 1),\n" +
            "        rating_count = pr.rating_count + 1\n" +
            "WHEN NOT MATCHED THEN\n" +
            "    INSERT (passenger_id, average_rating, rating_count)\n" +
            "    VALUES (src.passengerId, src.rating, 0)", nativeQuery = true)
    void updateOrSaveRating(@Param("passengerId") Long id, @Param("rating") double rating);

    @Query("SELECT pr FROM PassengerRating pr WHERE pr.deleted = false")
    List<PassengerRating> findAllNotDeleted();

    @Query("SELECT pr FROM PassengerRating pr WHERE pr.passengerId = :passengerId AND pr.deleted = false")
    Optional<PassengerRating> findRating(@Param("passengerId") Long id);
}
