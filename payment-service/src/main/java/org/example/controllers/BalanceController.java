package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.BalanceDTO;
import org.example.dto.ErrorResponse;
import org.example.services.BalanceService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/balance")
@AllArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    public ResponseEntity<ErrorResponse> create(@RequestBody BalanceDTO balanceDTO){
        try {
            balanceService.create(balanceDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IdentifierGenerationException e){
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{passengerId}")
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable("passengerId") Long passengerId){
        try{
            BalanceDTO balanceDTO = balanceService.getBalance(passengerId);
            return ResponseEntity.ok(balanceDTO);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/top-up/{passengerId}")
    public ResponseEntity<ErrorResponse> topUp(@PathVariable("passengerId") Long passengerId, @RequestParam("deposit") double deposit){
        try{
            balanceService.topUpBalance(passengerId, deposit);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{passengerId}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("passengerId") Long passengerId){
        try{
            balanceService.softDelete(passengerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
