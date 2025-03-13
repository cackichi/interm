package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.exceptions.InvalidRatingException;
import org.example.exceptions.NegativeCostException;
import org.example.exceptions.NoWaitingRideException;
import org.example.services.RideService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@AllArgsConstructor
@Tag(name = "Пользовательский контроллер", description = "Взаимодействие с поездками")
public class RideController {
    private final RideService rideService;

    @PostMapping
    @Operation(summary = "Создание поездки", description = "Позволяет создать новую поездку")
    public ResponseEntity<ErrorResponse> create(
            @RequestBody @Parameter(required = true) RideDTO rideDTO
    ) {
        rideService.create(rideDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск поездки", description = "Позволяет найти конкретную поездку")
    public ResponseEntity<RideDTO> findById(
            @PathVariable("id") @Parameter(description = "id искомой поездки", required = true) Long id
    ) {
        return ResponseEntity.ok(rideService.findById(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Редактирование поездки", description = "Позволяет отредактировать существующую поездку")
    public ResponseEntity<ErrorResponse> edit(
            @PathVariable("id") @Parameter(description = "id необходимой плоездки", required = true) Long id,
            @RequestBody @Parameter(required = true) RideDTO rideDTO
    ) {
        rideService.update(id, rideDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Мягкое удаление поездки", description = "Позволяет мягко удалить поездку из БД")
    public ResponseEntity<ErrorResponse> softDelete(
            @PathVariable("id") @Parameter(description = "id удаляемой поездки", required = true) Long id
    ) {
        rideService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Список поездок", description = "Позволяет найти все поездки с пагианцией")
    public ResponseEntity<RidePageDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Номер страницы пагинации") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Размер пагинации") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(rideService.findAllNotDeleted(pageable));
    }

    @PatchMapping("/start/{driverId}")
    @Operation(summary = "Поиск ожидающей поездки", description = "Позволяет найти поездку со статусом ожидания")
    public ResponseEntity<ErrorResponse> driverStartTravel(
            @PathVariable("driverId") @Parameter(description = "id ищущего водителя", required = true) String driverId
    ) throws NoWaitingRideException {
        rideService.checkFreeRide(driverId);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Поездка найдена, запрос на проверку id водителя отправлен"));
    }

    @PatchMapping("/stop/{driverId}")
    @Operation(summary = "Прекращение поездки", description = "Позволяет водителю прекратить поездку")
    public ResponseEntity<ErrorResponse> driverStopTravel(
            @PathVariable("driverId") @Parameter(description = "id прекращающего поездку водителя", required = true) String driverId,
            @RequestParam("rating") @Parameter(description = "рейтинг пассажира от водителя", required = true) double passengerRating,
            @RequestParam("cost") @Parameter(description = "стоимость поездки", required = true) double costOfRide
    ) throws InvalidRatingException, NegativeCostException {
        if (passengerRating < 0 || passengerRating > 5)
            throw new InvalidRatingException("Рейтинг должен быть в пределах [0,5]");
        if (costOfRide <= 0) throw new NegativeCostException("Цена должна быть больше 0");
        rideService.stopTravel(driverId, passengerRating, costOfRide);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Процесс завершения поездки начат"));
    }
}
