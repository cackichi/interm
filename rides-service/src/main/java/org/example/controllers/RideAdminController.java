package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.services.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/rides")
@AllArgsConstructor
@Tag(name = "Контроллер администратора", description = "Полное удаление поездки")
public class RideAdminController {
    private final RideService rideService;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Полное удаление поездки", description = "Позволяет полностью удалить поездку из БД")
    public ResponseEntity<ErrorResponse> hardDelete(
            @PathVariable("id") @Parameter(description = "id удаляемой поездки", required = true) Long id
    ){
        rideService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }
}
