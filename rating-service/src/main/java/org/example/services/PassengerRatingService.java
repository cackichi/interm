package org.example.services;

import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PassengerRatingService {
    PassengerRating mapToRating(PassengerRatingDTO dto);

    PassengerRatingDTO mapToDTO(PassengerRating passengerRating);

    double findRating(Long id);

    @Transactional
    void updateOrSaveRating(Long id, double rating);

    List<PassengerRating> findAllNotDeleted();

    void create(PassengerRatingDTO dto);

    @Transactional
    void softDelete(Long id);

    @Transactional
    void hardDelete(Long id);
}
