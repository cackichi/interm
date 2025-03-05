package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.exceptions.InsufficientBalanceException;
import org.example.services.PaymentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ErrorResponse> create(@RequestBody PaymentDTO paymentDTO){
        try {
            paymentService.create(paymentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping("/close/{passengerId}")
    public ResponseEntity<ErrorResponse> closePayment(@PathVariable("passengerId") Long passengerId){
        try {
            paymentService.closePayment(passengerId);
            return ResponseEntity.noContent().build();
        } catch (InsufficientBalanceException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/unpaid/{passengerId}")
    public ResponseEntity<PaymentPageDTO> getUnpaid(
            @PathVariable("passengerId") Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        try {
            Pageable pageable = PageRequest.of(page, size);
            PaymentPageDTO paymentPageDTO = paymentService.getUnpaid(passengerId, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(paymentPageDTO);
        } catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/paid/{passengerId}")
    public ResponseEntity<PaymentPageDTO> getPaid(
            @PathVariable("passengerId") Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(paymentService.getPaid(passengerId, pageable));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{passengerId}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("passengerId") Long passengerId){
        try {
            paymentService.softDelete(passengerId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
