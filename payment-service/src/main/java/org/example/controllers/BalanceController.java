package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.BalanceDTO;
import org.example.services.BalanceService;
import org.hibernate.id.IdentifierGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
@AllArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody BalanceDTO balanceDTO){
        try {
            balanceService.create(balanceDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IdentifierGenerationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-balance/{passengerId}")
    public ResponseEntity<BalanceDTO> getBalance(@PathVariable("passengerId") Long passengerId){
        try{
            BalanceDTO balanceDTO = balanceService.getBalance(passengerId);
            return ResponseEntity.ok(balanceDTO);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/top-up/{passengerId}")
    public ResponseEntity<HttpStatus> topUp(@PathVariable("passengerId") Long passengerId, @RequestParam("deposit") double deposit){
        try{
            balanceService.topUpBalance(passengerId, deposit);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/soft-delete/{passengerId}")
    public void softDelete(@PathVariable("passengerId") Long passengerId){
        balanceService.softDelete(passengerId);
    }

    @DeleteMapping("/hard-delete/{passengerId}")
    public void hardDelete(@PathVariable("passengerId") Long passengerId){
        balanceService.hardDelete(passengerId);
    }
}
