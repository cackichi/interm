package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.services.DriverServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/drivers")
@AllArgsConstructor
public class DriverAdminController {
    private final DriverServiceImpl driverServiceImpl;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ErrorResponse> hardDelete(@PathVariable("id") String id){
        try {
            driverServiceImpl.hardDelete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
