package org.example.controllers;

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
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    public ResponseEntity<ErrorResponse> create(@RequestBody BalanceDTO balanceDTO) {
        balanceService.create(balanceDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable("passengerId") Long passengerId) {
        BalanceDTO balanceDTO = balanceService.getBalance(passengerId);
        return ResponseEntity.ok(balanceDTO);
    }

    @PatchMapping("/top-up/{passengerId}")
    public ResponseEntity<ErrorResponse> topUp(@PathVariable("passengerId") Long passengerId, @RequestParam("deposit") double deposit) throws NegativeTopUpException {
        if(deposit <= 0) throw new NegativeTopUpException("Пополнение не может быть отрицательным");
        balanceService.topUpBalance(passengerId, deposit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{passengerId}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("passengerId") Long passengerId) {
        balanceService.softDelete(passengerId);
        return ResponseEntity.noContent().build();
    }
}
