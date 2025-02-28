package org.example.controllers;

import org.example.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.services.CarService;
import org.example.services.DriverService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@AllArgsConstructor
public class DriverController {
    private final DriverService driverService;
    private final CarService carService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ErrorResponse> create(@RequestBody DriverDTO driverDTO){
        if(driverDTO.getId() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Укажите id!");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try{
            driverService.create(driverDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ErrorResponse> edit(@RequestBody DriverDTO driverDTO, @PathVariable("id") String id){
        try {
            driverService.update(String.valueOf(id), driverDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> delete(@PathVariable("id") String id){
        try {
            driverService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<DriverPageDTO> findDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try {
            Pageable pageable = PageRequest.of(page, size);
            DriverPageDTO driverPageDTO = driverService.findAllNotDeleted(pageable);
            return ResponseEntity.ok(driverPageDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> findDriver(@PathVariable("id") String id){
        try{
            return ResponseEntity.ok(driverService.findById(id));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/cars")
    public ResponseEntity<CarPageDTO> findCarsOfDriver(
            @PathVariable("id") String driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
            Pageable pageable = PageRequest.of(page, size);
            CarPageDTO carPageDTO = carService.findCars(driverId, pageable);
            return ResponseEntity.ok(carPageDTO);
    }

    @DeleteMapping("/{driverId}/car/{number}")
    public ResponseEntity<ErrorResponse> deleteCar(@PathVariable("driverId") String id, @PathVariable("number") String number){
        try {
            carService.removeCarFromDriver(id, number);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{driverId}/car/{number}")
    public ResponseEntity<CarDTO> findCar(@PathVariable("driverId") String id, @PathVariable("number") String number){
        try{
            return ResponseEntity.ok(carService.findCar(id, number));
        } catch (NotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{driverId}/car")
    public ResponseEntity<ErrorResponse> createCar(@PathVariable("driverId") String driverId, @RequestBody CarDTO carDTO){
        if(carDTO.getNumber() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Укажите id!");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try{
            carService.create(driverId, carDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PatchMapping("/{driverId}/car")
    public ResponseEntity<ErrorResponse> updateCar(@PathVariable("driverId") String driverId, @RequestBody CarDTO carDTO) {
        try{
            carService.updateCar(driverId, carDTO);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
