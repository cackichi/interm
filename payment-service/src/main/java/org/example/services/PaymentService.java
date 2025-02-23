package org.example.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.example.dto.PaymentDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private final EntityManager entityManager;

    public Payment mapToPayment(PaymentDTO paymentDTO){
        return modelMapper.map(paymentDTO, Payment.class);
    }

    public PaymentDTO mapToDTO(Payment payment){
        return modelMapper.map(payment, PaymentDTO.class);
    }

    public void create(PaymentDTO paymentDTO){
        paymentDTO.setStatus(Status.WAITING);
        paymentRepository.save(mapToPayment(paymentDTO));
    }

    @Transactional
    public void closePayment(Long passengerId) throws InsufficientBalanceException {
        TypedQuery<Payment> paymentQuery = entityManager.createQuery(
                "SELECT p FROM Payment p WHERE p.passengerId = :passengerId AND p.status != 'PAID'",
                Payment.class
        );
        paymentQuery.setParameter("passengerId", passengerId);

        TypedQuery<Balance> balanceQuery = entityManager.createQuery(
                "SELECT b FROM Balance b WHERE b.passengerId = :passengerId",
                Balance.class
        );
        balanceQuery.setParameter("passengerId", passengerId);

        Payment payment = paymentQuery.getSingleResult();
        Balance balance = balanceQuery.getSingleResult();

        if (balance.getBalance() >= payment.getCost()) {
            balance.setBalance(balance.getBalance() - payment.getCost());
            payment.setStatus(Status.PAID);

            entityManager.merge(payment);
            entityManager.merge(balance);
        } else {
            throw new InsufficientBalanceException("Недостаточно средств на балансе");
        }

    }

    public PaymentDTO getUnpaid(Long passengerId){
        return paymentRepository.getUnpaid(passengerId).map(this::mapToDTO).orElseThrow(() -> new EntityNotFoundException("Не найдено не оплаченых"));
    }

    public List<Payment> getPaid(Long passengerId){
        return paymentRepository.getPaid(passengerId);
    }

    @Transactional
    public void softDelete(Long passengerId){
        paymentRepository.softDelete(passengerId);
    }

    @Transactional
    public void hardDelete(Long passengerId){
        paymentRepository.deleteByPassengerId(passengerId);
    }
}
