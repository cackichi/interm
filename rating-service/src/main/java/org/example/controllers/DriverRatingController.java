package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользовательский контроллер водительских оценок", description = "Взаимодействие с рейтингом водителя")
public class DriverRatingController {
    private final DriverRatingService driverRatingService;

    @GetMapping("/{id}")
    @Operation(summary = "Поиск рейтинга", description = "Позволяет найти оценку конкретного водителя")
    public ResponseEntity<ErrorResponse> findRating(
            @PathVariable("id") @Parameter(description = "id водителя", required = true) String id
    ) {
        return ResponseEntity.ok(new ErrorResponse("Рейтинг водителя с id" + ":" + id + " = " + driverRatingService.findRating(id)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Изменение рейтинга", description = "Позволяет обновить рейтинг конкретного водителя")
    public ResponseEntity<HttpStatus> updateRating(
            @PathVariable("id") @Parameter(description = "id водителя", required = true) String id,
            @RequestParam("rating") @Parameter(description = "оценка", required = true) double rating
    ) throws RatingInvalidException {
        if (rating < 0 || rating > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        driverRatingService.updateOrSaveRating(id, rating);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Мягкое удаление оценок", description = "Позволяет мягко удалить рейтинг водителя")
    public ResponseEntity<HttpStatus> softDelete(
            @PathVariable("id") @Parameter(description = "id водителя", required = true) String id
    ) {
        driverRatingService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Создание рейтинга", description = "Позволяет создать рейтинг водителя")
    public ResponseEntity<HttpStatus> create(
            @RequestBody @Parameter(required = true) DriverRatingDTO dto
    ) throws RatingInvalidException {
        double avg = dto.getAverageRating();
        if (avg < 0 || avg > 5) throw new RatingInvalidException("Рейтинг должен быть в диапазоне 0-5");
        if (dto.getDriverId() == null) throw new IdentifierGenerationException("Вы не указали id!");
        driverRatingService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
