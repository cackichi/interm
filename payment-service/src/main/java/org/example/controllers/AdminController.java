package org.example.controllers;

import lombok.AllArgsConstructor;
import org.example.services.BalanceService;
import org.example.services.PaymentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final BalanceService balanceService;
    private final PaymentService paymentService;

    @DeleteMapping("/balance/{passengerId}")
    public void hardDeleteBalance(@PathVariable("passengerId") Long passengerId){
        balanceService.hardDelete(passengerId);
    }

    @DeleteMapping("/payment/{passengerId}")
    public void hardDeletePayment(@PathVariable("passengerId") Long passengerId){
        paymentService.hardDelete(passengerId);
    }
}
