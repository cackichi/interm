package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.services.BalanceService;
import org.example.services.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
@Tag(name = "Контроллер администратора", description = "Полное удаление баланса и платежа")
public class AdminController {
    private final BalanceService balanceService;
    private final PaymentService paymentService;

    @DeleteMapping("/balance/{passengerId}")
    @Operation(summary = "Полное удаление баланса", description = "Позволяет полностью удалить баланс пассажира")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteBalance(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId
    ) {
        balanceService.hardDelete(passengerId);
    }

    @DeleteMapping("/payment/{passengerId}")
    @Operation(summary = "Полное удаление плажтежа", description = "Позволяет полностью удалить платеж пассажира")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeletePayment(
            @PathVariable("passengerId") @Parameter(description = "id пассажира", required = true) Long passengerId
    ) {
        paymentService.hardDelete(passengerId);
    }
}
