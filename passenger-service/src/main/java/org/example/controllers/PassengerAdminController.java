package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.services.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/passenger")
@AllArgsConstructor
public class PassengerAdminController {
    private final PassengerService passengerService;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDelete(@PathVariable("id") Long id){
        passengerService.hardDelete(id);
    }
}
