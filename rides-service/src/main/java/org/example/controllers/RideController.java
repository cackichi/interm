package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.RideDTO;
import org.example.entities.Ride;
import org.example.services.RideService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ride")
@AllArgsConstructor
public class RideController {
    private final RideService rideService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody RideDTO rideDTO){
        try {
            if(rideDTO.getPassengerId() == null) throw new IdentifierGenerationException("Вы не задали id!");
            rideService.create(rideDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IdentifierGenerationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<RideDTO> findById(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(rideService.findById(id));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/edit/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void edit(@PathVariable("id") Long id, @RequestBody RideDTO rideDTO){
        rideService.update(id, rideDTO);
    }

    @DeleteMapping("/soft-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable("id") Long id){
        rideService.softDelete(id);
    }

    @DeleteMapping("/hard-delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable("id") Long id){
        rideService.hardDelete(id);
    }

    @GetMapping("/find-all")
    public List<Ride> findAll(){
        return rideService.findAllNotDeleted();
    }
}
