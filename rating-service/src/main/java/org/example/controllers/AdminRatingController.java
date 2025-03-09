package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.services.DriverRatingService;
import org.example.services.PassengerRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
@Tag(name = "Контроллер администратора", description = "Полное удаление рейтингов")
public class AdminRatingController {
    private final DriverRatingService driverRatingService;
    private final PassengerRatingService passengerRatingService;

    @DeleteMapping("/drivers/rating/{id}")
    @Operation(summary = "Полное удаление рейтинга водителя", description = "Позволяет полностью удалить оценки водителя из БД")
    public ResponseEntity<HttpStatus> hardDeleteDriverRating(
            @PathVariable("id") @Parameter(description = "id неодходимого водителя", required = true) String id
    ) {
        driverRatingService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/passenger/rating/{id}")
    @Operation(summary = "Полное удаление рейтинга пассажира", description = "Позволяет полностью удалить оценки пассажира из БД")
    public ResponseEntity<HttpStatus> hardDeletePassengerRating(
            @PathVariable("id") @Parameter(description = "id неодходимого пассажира", required = true) Long id
    ) {
        passengerRatingService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
