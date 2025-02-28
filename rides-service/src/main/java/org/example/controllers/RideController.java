package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
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
        try {
            rideService.create(rideDTO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideDTO> findById(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(rideService.findById(id));
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ErrorResponse> edit(@PathVariable("id") Long id, @RequestBody RideDTO rideDTO){
        try {
            rideService.update(id, rideDTO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("id") Long id){
        try {
            rideService.softDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<RidePageDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        try {
            return ResponseEntity.ok(rideService.findAllNotDeleted(pageable));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
