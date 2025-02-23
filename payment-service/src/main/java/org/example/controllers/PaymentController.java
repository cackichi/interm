package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.PaymentDTO;
import org.example.entities.Payment;
import org.example.exceptions.CreatePaymentException;
import org.example.exceptions.InsufficientBalanceException;
import org.example.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody PaymentDTO paymentDTO){
        try {
            paymentService.create(paymentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CreatePaymentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/close/{passengerId}")
    public ResponseEntity<String> closePayment(@PathVariable("passengerId") Long passengerId){
        try {
            paymentService.closePayment(passengerId);
            return ResponseEntity.noContent().build();
        } catch (InsufficientBalanceException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/unpaid/{passengerId}")
    public ResponseEntity<PaymentDTO> getUnpaid(@PathVariable("passengerId") Long passengerId){
        try {
            PaymentDTO paymentDTO = paymentService.getUnpaid(passengerId);
            return ResponseEntity.status(HttpStatus.OK).body(paymentDTO);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/paid/{passengerId}")
    public List<Payment> getPaid(@PathVariable("passengerId") Long passengerId){
        return paymentService.getPaid(passengerId);
    }

    @DeleteMapping("/soft-delete/{passengerId}")
    public void softDelete(@PathVariable("passengerId") Long passengerId){
        paymentService.softDelete(passengerId);
    }

    @DeleteMapping("/hard-delete/{passengerId}")
    public void hardDelete(@PathVariable("passengerId") Long passengerId){
        paymentService.hardDelete(passengerId);
    }
}
