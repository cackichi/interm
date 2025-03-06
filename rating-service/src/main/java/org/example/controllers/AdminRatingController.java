package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.services.DriverRatingService;
import org.example.services.PassengerRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminRatingController {
    private final DriverRatingService driverRatingService;
    private final PassengerRatingService passengerRatingService;

    @DeleteMapping("/drivers/rating/{id}")
    public ResponseEntity<HttpStatus> hardDeleteDriverRating(@PathVariable("id") String id) {
        driverRatingService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/passenger/rating/{id}")
    public ResponseEntity<HttpStatus> hardDeletePassengerRating(@PathVariable("id") Long id) {
        passengerRatingService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
