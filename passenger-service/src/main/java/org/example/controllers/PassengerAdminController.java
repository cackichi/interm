package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.services.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/passenger")
@AllArgsConstructor
@Tag(name = "Контроллер администратора", description = "Полное удаление пассажира")
public class PassengerAdminController {
    private final PassengerService passengerService;
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление пассажира", description = "Позволяет полностью удалить пассажира")
    public void hardDelete(
            @PathVariable("id") @Parameter(description = "id удаляемого пассажира", required = true) Long id
    ){
        passengerService.hardDelete(id);
    }
}
