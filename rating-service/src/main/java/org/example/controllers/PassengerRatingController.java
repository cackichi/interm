package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.dto.PassengerRatingDTO;
import org.example.exceptions.RatingInvalidException;
import org.example.services.PassengerRatingService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passenger/rating")
@AllArgsConstructor
public class PassengerRatingController {
    private final PassengerRatingService passengerRatingService;

    @GetMapping("/{id}")
    public ResponseEntity<ErrorResponse> findRating(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(new ErrorResponse("Рейтинг пассажира с id" + ":" + id + " = " + passengerRatingService.findRating(id)));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ErrorResponse> updateRating(@PathVariable("id") Long id, @RequestParam("rating") double rating){
        try {
            if(rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
            passengerRatingService.updateOrSaveRating(id, rating);
            return ResponseEntity.noContent().build();
        } catch (RatingInvalidException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("id") Long id){
        try{
            passengerRatingService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ErrorResponse> create(@RequestBody PassengerRatingDTO passengerRatingDTO){
        double avg = passengerRatingDTO.getAverageRating();
        try {
            if(avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
            if(passengerRatingDTO.getPassengerId() == null) throw new IdentifierGenerationException("Вы не указали id!");
            passengerRatingService.create(passengerRatingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RatingInvalidException | IdentifierGenerationException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
