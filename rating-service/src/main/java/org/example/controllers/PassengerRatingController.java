package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользовательский контроллер пассажирских оценок", description = "Взаимодействие с рейтингом пассажира")
public class PassengerRatingController {
    private final PassengerRatingService passengerRatingService;

    @GetMapping("/{id}")
    @Operation(summary = "Поиск рейтинга", description = "Позволяет найти оценку конкретного пассажира")
    public ResponseEntity<ErrorResponse> findRating(
            @PathVariable("id") @Parameter(description = "id пассажира", required = true) Long id
    ) {
        return ResponseEntity.ok(new ErrorResponse("Рейтинг пассажира с id" + ":" + id + " = " + passengerRatingService.findRating(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление рейтинга", description = "Позволяет обновить рейтинг конкретного пассажира")
    public ResponseEntity<HttpStatus> updateRating(
            @PathVariable("id") @Parameter(description = "id пассажира", required = true) Long id,
            @RequestParam("rating") @Parameter(description = "оценка", required = true) double rating
    ) throws RatingInvalidException {
        if (rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        passengerRatingService.updateOrSaveRating(id, rating);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Мягкое удаления рейтинга", description = "Позволяет мягко удалить конкретного пассажира")
    public ResponseEntity<HttpStatus> softDelete(
            @PathVariable("id") @Parameter(description = "id пассажира", required = true) Long id
    ) {
        passengerRatingService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Создание рейтинга", description = "Позволяет создать рейтинг конкретному водителю")
    public ResponseEntity<HttpStatus> create(@RequestBody PassengerRatingDTO passengerRatingDTO) throws RatingInvalidException {
        double avg = passengerRatingDTO.getAverageRating();
        if (avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        if (passengerRatingDTO.getPassengerId() == null) throw new IdentifierGenerationException("Вы не указали id!");
        passengerRatingService.create(passengerRatingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
