package org.example.controllers;

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
public class RideController {
    private final RideService rideService;

    @PostMapping
    public ResponseEntity<ErrorResponse> create(@RequestBody RideDTO rideDTO){
        rideService.create(rideDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(rideService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ErrorResponse> edit(@PathVariable("id") Long id, @RequestBody RideDTO rideDTO){
        rideService.update(id, rideDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("id") Long id){
        rideService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<RidePageDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(rideService.findAllNotDeleted(pageable));
    }

    @PatchMapping("/start/{driverId}")
    public ResponseEntity<ErrorResponse> driverStartTravel(@PathVariable("driverId") String driverId) throws NoWaitingRideException {
        rideService.checkFreeRide(driverId);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Поездка найдена, запрос на проверку id водителя отправлен"));
    }

    @PatchMapping("/stop/{driverId}")
    public ResponseEntity<ErrorResponse> driverStopTravel(
            @PathVariable("driverId") String driverId,
            @RequestParam("rating") double passengerRating,
            @RequestParam("cost") double costOfRide
    ) throws InvalidRatingException, NegativeCostException {
        if(passengerRating < 0 || passengerRating > 5) throw new InvalidRatingException("Рейтинг должен быть в пределах [0,5]");
        if(costOfRide <= 0) throw new NegativeCostException("Цена должна быть больше 0");
        rideService.stopTravel(driverId, passengerRating, costOfRide);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Процесс завершения поездки начат"));
    }
}
