package org.example.services;

import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Payment;
import org.example.exceptions.InsufficientBalanceException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    Payment mapToPayment(PaymentDTO paymentDTO);

    PaymentDTO mapToDTO(Payment payment);

    void create(PaymentDTO paymentDTO);

    void closePayment(Long passengerId) throws InsufficientBalanceException;

    PaymentPageDTO getUnpaid(Long passengerId, Pageable pageable);

    PaymentPageDTO getPaid(Long passengerId, Pageable pageable);

    PaymentPageDTO getPage(List<Payment> payments, Pageable pageable);

    void softDelete(Long passengerId);

    void hardDelete(Long passengerId);
}
