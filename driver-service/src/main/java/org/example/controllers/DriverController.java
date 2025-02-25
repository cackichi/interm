package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.services.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/driver")
@AllArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> create(@RequestBody DriverDTO driverDTO){
        if(driverDTO.getId() == null) return ResponseEntity.badRequest().body("Укажите id!");
        driverService.create(driverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void edit(@RequestBody DriverDTO driverDTO, @PathVariable("id") String id){
        driverService.update(String.valueOf(id), driverDTO);
    }

    @DeleteMapping("/soft-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id){
        driverService.softDelete(id);
    }

    @GetMapping("/find-all")
    public List<Driver> findAll(){
        return driverService.findAllNotDeleted();
    }

    @DeleteMapping("/hard-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable("id") String id){
        driverService.hardDelete(id);
    }
}
