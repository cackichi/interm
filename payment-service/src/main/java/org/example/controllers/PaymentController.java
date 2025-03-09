package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользовательский контроллер платежей", description = "Взаимодействие с платежами пассажира")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Создание платежа", description = "Позволяет создать платеж пассажиру")
    public ResponseEntity<ErrorResponse> create(
            @RequestBody @Parameter(required = true) PaymentDTO paymentDTO
    ) {
        paymentService.create(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/close/{passengerId}")
    @Operation(summary = "Закратие платежа", description = "Позволяет закрыть платеж пассажира")
    public ResponseEntity<ErrorResponse> closePayment(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId
    ) throws InsufficientBalanceException {
        paymentService.closePayment(passengerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unpaid/{passengerId}")
    @Operation(summary = "Список незакрытых платежей", description = "Позволяет найти список незакрытых платежей пассажира с пагинацией")
    public ResponseEntity<PaymentPageDTO> getUnpaid(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "номер страницы пагинации") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "размер пагинации", required = true) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaymentPageDTO paymentPageDTO = paymentService.getUnpaid(passengerId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(paymentPageDTO);
    }

    @GetMapping("/paid/{passengerId}")
    @Operation(summary = "Список закрытых платежей", description = "Позволяет найти список закрытых платежей пассажира с пагинацие")
    public ResponseEntity<PaymentPageDTO> getPaid(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId,
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "номер страницы пагинации") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "размер пагинации", required = true) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.getPaid(passengerId, pageable));
    }

    @DeleteMapping("/{passengerId}")
    @Operation(summary = "Мягкое удаление платежа", description = "Позволяет мягко удалить платеж")
    public ResponseEntity<ErrorResponse> softDelete(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId
    ) {
        paymentService.softDelete(passengerId);
        return ResponseEntity.noContent().build();
    }
}
