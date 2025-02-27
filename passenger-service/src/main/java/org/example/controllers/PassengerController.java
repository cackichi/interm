package org.example.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.dto.PassengerDTO;
import org.example.entities.Passenger;
import org.example.services.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pass")
@AllArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerDTO createPassenger(@RequestBody PassengerDTO passengerDTO){
        Passenger passenger = passengerService.save(passengerDTO);
        return passengerService.mapToDTO(passenger);
    }

    @PatchMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPassenger(@RequestBody PassengerDTO edit, @PathVariable("id") Long id){
        passengerService.updatePass(id, edit);
    }

    @DeleteMapping("/soft-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeletePassenger(@PathVariable("id") Long id){
        passengerService.softDelete(id);
    }

    @GetMapping("/find-all")
    public List<Passenger> findAll(){
        return passengerService.findAllNotDeleted();
    }

    @DeleteMapping("/hard-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable("id") Long id){
        passengerService.hardDelete(id);
    }

}
