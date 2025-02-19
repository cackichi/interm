package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.collections.Car;
import org.example.dto.CarDTO;
import org.example.services.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car")
@AllArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CarDTO carDTO){
        carService.createCar(carDTO, carDTO.getDriverId());
    }

    @PatchMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void edit(@RequestBody CarDTO carDTO, @PathVariable("id") String id){
        carService.update(id ,carDTO);
    }

    @DeleteMapping("/soft-delete/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable("driverId") String driverId){
        carService.softDelete(driverId);
    }

    @GetMapping("/find-all")
    public List<Car> findAll(){
        return carService.findAllNotDeleted();
    }
}
