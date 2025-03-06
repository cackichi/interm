package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dto.DriverRatingDTO;
import org.example.dto.ErrorResponse;
import org.example.exceptions.RatingInvalidException;
import org.example.services.DriverRatingService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers/rating")
@AllArgsConstructor
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @GetMapping("/{id}")
    public ResponseEntity<ErrorResponse> findRating(@PathVariable("id") String id) {
        return ResponseEntity.ok(new ErrorResponse("Рейтинг водителя с id" + ":" + id + " = " + driverRatingService.findRating(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updateRating(@PathVariable("id") String id, @RequestParam("rating") double rating) throws RatingInvalidException {
        if (rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        driverRatingService.updateOrSaveRating(id, rating);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> softDelete(@PathVariable("id") String id) {
        driverRatingService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody DriverRatingDTO dto) throws RatingInvalidException {
        double avg = dto.getAverageRating();
        if (avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        if (dto.getDriverId() == null) throw new IdentifierGenerationException("Вы не указали id!");
        driverRatingService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
