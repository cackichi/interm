package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.services.DriverServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/drivers")
@AllArgsConstructor
@Tag(name = "Контроллер администратора", description = "Полное удаление водителя")
public class DriverAdminController {
    private final DriverServiceImpl driverServiceImpl;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Полное удаление водителя", description = "Позволяет полностью удалить водителя из БД")
    public ResponseEntity<HttpStatus> hardDelete(
            @PathVariable("id") @Parameter(description = "id удаляемого водителя", required = true) String id
    ){
            driverServiceImpl.hardDelete(id);
            return ResponseEntity.noContent().build();
    }
}
