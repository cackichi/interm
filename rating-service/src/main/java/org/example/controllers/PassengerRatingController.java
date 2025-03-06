package org.example.controllers;

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
    public ResponseEntity<ErrorResponse> findRating(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new ErrorResponse("Рейтинг пассажира с id" + ":" + id + " = " + passengerRatingService.findRating(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateRating(@PathVariable("id") Long id, @RequestParam("rating") double rating) throws RatingInvalidException {
        if (rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        passengerRatingService.updateOrSaveRating(id, rating);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> softDelete(@PathVariable("id") Long id) {
        passengerRatingService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody PassengerRatingDTO passengerRatingDTO) throws RatingInvalidException {
        double avg = passengerRatingDTO.getAverageRating();
        if (avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        if (passengerRatingDTO.getPassengerId() == null) throw new IdentifierGenerationException("Вы не указали id!");
        passengerRatingService.create(passengerRatingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
