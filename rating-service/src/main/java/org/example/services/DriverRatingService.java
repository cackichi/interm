package org.example.services;

import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;

import java.util.List;

public interface DriverRatingService {

    DriverRating mapToRating(DriverRatingDTO dto);

    DriverRatingDTO mapToDTO(DriverRating driverRating);

    double findRating(String id);

    void updateOrSaveRating(String id, double rating);

    List<DriverRating> findAllNotDeleted();

    void create(DriverRatingDTO dto);

    void softDelete(String id);

    void hardDelete(String id);
}
