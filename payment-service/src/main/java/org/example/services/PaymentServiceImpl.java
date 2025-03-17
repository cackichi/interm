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
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.PaymentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Payment mapToPayment(PaymentDTO paymentDTO){
        return modelMapper.map(paymentDTO, Payment.class);
    }
    @Override
    public PaymentDTO mapToDTO(Payment payment){
        return modelMapper.map(payment, PaymentDTO.class);
    }
    @Override
    @Transactional
    public Payment create(PaymentDTO paymentDTO) {
        paymentDTO.setStatus(Status.WAITING);
        return paymentRepository.save(mapToPayment(paymentDTO));
    }
    @Override
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
    @Override
    public PaymentPageDTO getUnpaid(Long passengerId, Pageable pageable){
        List<Payment> unpaidPayments = paymentRepository.getUnpaid(passengerId);
        return getPage(unpaidPayments, pageable);
    }
    @Override
    public PaymentPageDTO getPaid(Long passengerId, Pageable pageable){
        List<Payment> paidPayments = paymentRepository.getPaid(passengerId);
        return getPage(paidPayments, pageable);
    }

    @Override
    public PaymentPageDTO getPage(List<Payment> payments, Pageable pageable){
        int totalPayments = payments.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), totalPayments);

        List<PaymentDTO> paymentDTOs = payments.subList(start, end).stream()
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
    @Override
    @Transactional
    public void softDelete(Long passengerId){
        paymentRepository.softDelete(passengerId);
    }
    @Override
    @Transactional
    public void hardDelete(Long passengerId){
        paymentRepository.deleteByPassengerId(passengerId);
    }
}
