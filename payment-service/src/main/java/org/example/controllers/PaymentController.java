package org.example.controllers;

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
    public ResponseEntity<ErrorResponse> create(@RequestBody PaymentDTO paymentDTO) {
        paymentService.create(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/close/{passengerId}")
    public ResponseEntity<ErrorResponse> closePayment(@PathVariable("passengerId") Long passengerId) throws InsufficientBalanceException {
        paymentService.closePayment(passengerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unpaid/{passengerId}")
    public ResponseEntity<PaymentPageDTO> getUnpaid(
            @PathVariable("passengerId") Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaymentPageDTO paymentPageDTO = paymentService.getUnpaid(passengerId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(paymentPageDTO);
    }

    @GetMapping("/paid/{passengerId}")
    public ResponseEntity<PaymentPageDTO> getPaid(
            @PathVariable("passengerId") Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.getPaid(passengerId, pageable));
    }

    @DeleteMapping("/{passengerId}")
    public ResponseEntity<ErrorResponse> softDelete(@PathVariable("passengerId") Long passengerId) {
        paymentService.softDelete(passengerId);
        return ResponseEntity.noContent().build();
    }
}
