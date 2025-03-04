package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.services.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/rides")
@AllArgsConstructor
public class RideAdminController {
    private final RideService rideService;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ErrorResponse> hardDelete(@PathVariable("id") Long id){
        rideService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
