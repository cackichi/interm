package org.example.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.exceptions.CreatePaymentException;
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public void create(PaymentDTO paymentDTO) throws CreatePaymentException {
        paymentDTO.setStatus(Status.WAITING);
        if(paymentRepository.createIfNoPaidPayments(paymentDTO.getPassengerId(), paymentDTO.getRideId(), paymentDTO.getCost()) == 0) throw new CreatePaymentException("У вас есть неоплаченные платежи");
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

        Payment payment = paymentQuery.getResultList().stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Нет не оплаченных платежей"));
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

    public PaymentPageDTO getPaid(Long passengerId, Pageable pageable){
        List<Payment> paidPayments = paymentRepository.getPaid(passengerId);
        int totalPayments = paidPayments.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), totalPayments);

        List<PaymentDTO> paymentDTOs = paidPayments.subList(start, end).stream()
                .map(this::mapToDTO)
                .toList();

        return new PaymentPageDTO(
                paymentDTOs,
                totalPayments,
                (int) Math.ceil((double) totalPayments / pageable.getPageSize()),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
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
