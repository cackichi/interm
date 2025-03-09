package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.dto.BalanceDTO;
import org.example.dto.ErrorResponse;
import org.example.exceptions.NegativeTopUpException;
import org.example.services.BalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@AllArgsConstructor
@Tag(name = "Пользовательский контроллер баланса", description = "Взаимодействие с балансом пассажира")
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    @Operation(summary = "Создание баланса", description = "Позволяет создать баланс пассажиру")
    public ResponseEntity<ErrorResponse> create(
            @RequestBody @Parameter(required = true) BalanceDTO balanceDTO
    ) {
        balanceService.create(balanceDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{passengerId}")
    @Operation(summary = "Поиск баланса", description = "Позволяет получить баланс пассажира")
    public ResponseEntity<BalanceDTO> getBalance(
            @PathVariable("passengerId") @Parameter(description = "id неодходимого пассажира", required = true) Long passengerId
    ) {
        BalanceDTO balanceDTO = balanceService.getBalance(passengerId);
        return ResponseEntity.ok(balanceDTO);
    }

    @PatchMapping("/top-up/{passengerId}")
    @Operation(summary = "Обновление баланса", description = "Позволяет пополнить баланс пассажиру")
    public ResponseEntity<ErrorResponse> topUp(
            @PathVariable("passengerId") @Parameter(description = "id неодходимого пассажира", required = true) Long passengerId,
            @RequestParam("deposit") @Parameter(description = "сумма пополнения", required = true) double deposit
    ) throws NegativeTopUpException {
        if(deposit <= 0) throw new NegativeTopUpException("Пополнение не может быть отрицательным");
        balanceService.topUpBalance(passengerId, deposit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{passengerId}")
    @Operation(summary = "Мягкое удаление баланса", description = "Позволяет мягко удалить баланс")
    public ResponseEntity<ErrorResponse> softDelete(
            @PathVariable("passengerId") @Parameter(description = "id неодходимого пассажира", required = true) Long passengerId
    ) {
        balanceService.softDelete(passengerId);
        return ResponseEntity.noContent().build();
    }
}
