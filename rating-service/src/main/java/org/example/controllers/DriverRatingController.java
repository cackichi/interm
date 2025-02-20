package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.DriverRatingDTO;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.DriverRating;
import org.example.entities.PassengerRating;
import org.example.exceptions.RatingInvalidException;
import org.example.services.DriverRatingService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rating/driver")
@AllArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @GetMapping("/find/{id}")
    public String findRating(@PathVariable("id") Long id){
        try {
            return "Рейтинг водителя с id" + ":" + id + " = " + driverRatingService.findRating(id);
        } catch (EntityNotFoundException e){
            return e.getMessage();
        }
    }

    @PatchMapping("/update-rating/{id}")
    public ResponseEntity<String> updateRating(@PathVariable("id") Long id, @RequestParam("rating") double rating){
        try {
            if(rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
            driverRatingService.updateRating(id, rating);
            return ResponseEntity.noContent().build();
        } catch (RatingInvalidException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/soft-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable("id") Long id){
        driverRatingService.softDelete(id);
    }

    @DeleteMapping("/hard-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable("id") Long id){
        driverRatingService.hardDelete(id);
    }

    @GetMapping("/find-all")
    public List<DriverRating> findAll(){
        return driverRatingService.findAllNotDeleted();
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody DriverRatingDTO dto){
        double avg = dto.getAverageRating();
        try {
            if(avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
            if(dto.getDriverId() == null) throw new IdentifierGenerationException("Вы не указали id!");
            driverRatingService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RatingInvalidException | IdentifierGenerationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
