package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
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
    public ResponseEntity<ErrorResponse> hardDeleteDriverRating(@PathVariable("id") Long id){
        try{
            driverRatingService.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
    @DeleteMapping("/passenger/rating/{id}")
    public ResponseEntity<ErrorResponse> hardDeletePassengerRating(@PathVariable("id") Long id){
        try{
            passengerRatingService.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
