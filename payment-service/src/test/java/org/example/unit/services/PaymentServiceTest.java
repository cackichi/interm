package org.example.unit.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.PaymentRepository;
import org.example.services.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    private Payment payment;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        payment = new Payment(
                1L,
                1L,
                1L,
                350,
                Status.PAID,
                false
        );
        paymentDTO = new PaymentDTO(
                1L,
                1L,
                1L,
                350,
                Status.PAID,
                false
        );
    }

    @Test
    void mapToPayment() {
        when(modelMapper.map(paymentDTO, Payment.class))
                .thenReturn(payment);

        Payment res = paymentService.mapToPayment(paymentDTO);

        assertThat(res)
                .isNotNull()
                .isEqualTo(payment);

        verify(modelMapper).map(paymentDTO, Payment.class);
    }

    @Test
    void mapToDTO() {
        when(modelMapper.map(payment, PaymentDTO.class))
                .thenReturn(paymentDTO);

        PaymentDTO res = paymentService.mapToDTO(payment);

        assertThat(res)
                .isNotNull()
                .isEqualTo(paymentDTO);

        verify(modelMapper).map(payment, PaymentDTO.class);
    }

    @Test
    void create() {
        when(modelMapper.map(paymentDTO, Payment.class))
                .thenReturn(payment);
        paymentService.create(paymentDTO);

        assertThat(paymentDTO.getStatus())
                .isEqualTo(Status.WAITING);

        verify(paymentRepository).save(payment);
    }

    @Test
    void closePayment() throws InsufficientBalanceException {
        Balance balance = new Balance();
        balance.setBalance(1000);
        balance.setPassengerId(1L);

        TypedQuery<Payment> paymentQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Payment.class)))
                .thenReturn(paymentQuery);
        when(paymentQuery.setParameter(eq("passengerId"), eq(balance.getPassengerId())))
                .thenReturn(paymentQuery);
        when(paymentQuery.getResultList())
                .thenReturn(List.of(payment));

        TypedQuery<Balance> balanceQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Balance.class)))
                .thenReturn(balanceQuery);
        when(balanceQuery.setParameter(eq("passengerId"), eq(balance.getPassengerId())))
                .thenReturn(balanceQuery);
        when(balanceQuery.getSingleResult())
                .thenReturn(balance);

        when(entityManager.merge(eq(payment)))
                .thenReturn(payment);
        when(entityManager.merge(eq(balance)))
                .thenReturn(balance);

        paymentService.closePayment(balance.getPassengerId());

        verify(entityManager).createQuery(anyString(), eq(Payment.class));
        verify(entityManager).createQuery(anyString(), eq(Balance.class));
        verify(entityManager).merge(payment);
        verify(entityManager).merge(balance);
        assertThat(payment.getStatus()).isEqualTo(Status.PAID);
    }

    @Test
    void closePayment_InsufficientBalance(){
        Balance balance = new Balance();
        balance.setBalance(100);
        balance.setPassengerId(1L);

        TypedQuery<Payment> paymentQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Payment.class)))
                .thenReturn(paymentQuery);
        when(paymentQuery.setParameter(eq("passengerId"), eq(balance.getPassengerId())))
                .thenReturn(paymentQuery);
        when(paymentQuery.getResultList())
                .thenReturn(List.of(payment));

        TypedQuery<Balance> balanceQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(Balance.class)))
                .thenReturn(balanceQuery);
        when(balanceQuery.setParameter(eq("passengerId"), eq(balance.getPassengerId())))
                .thenReturn(balanceQuery);
        when(balanceQuery.getSingleResult())
                .thenReturn(balance);

        assertThrows(InsufficientBalanceException.class,
                () -> paymentService.closePayment(balance.getPassengerId()));
    }

    @Test
    void getUnpaid() {
        List<Payment> paidPayments = List.of(
                new Payment(1L, payment.getPassengerId(), 1L, 350, Status.WAITING, false),
                new Payment(2L, payment.getPassengerId(), 1L, 200, Status.WAITING, false)
        );

        Pageable pageable = PageRequest.of(0, 10);

        when(paymentRepository.getPaid(payment.getPassengerId()))
                .thenReturn(paidPayments);

        PaymentPageDTO page = paymentService.getPaid(payment.getPassengerId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        verify(paymentRepository).getPaid(payment.getPassengerId());
    }

    @Test
    void getPaid() {
        List<Payment> paidPayments = List.of(
                new Payment(1L, payment.getPassengerId(), 1L, 350, Status.PAID, false),
                new Payment(2L, payment.getPassengerId(), 1L, 200, Status.PAID, false)
        );

        Pageable pageable = PageRequest.of(0, 10);

        when(paymentRepository.getPaid(payment.getPassengerId()))
                .thenReturn(paidPayments);

        PaymentPageDTO page = paymentService.getPaid(payment.getPassengerId(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        verify(paymentRepository).getPaid(payment.getPassengerId());
    }

    @Test
    void getPage() {
        List<Payment> payments = List.of(
                new Payment(1L, 1L, 1L, 350, Status.PAID, false),
                new Payment(2L, 1L, 1L, 200, Status.PAID, false),
                new Payment(3L, 1L, 1L, 150, Status.PAID, false)
        );

        Pageable pageable = PageRequest.of(0, 2);

        when(modelMapper.map(payment, PaymentDTO.class))
                .thenReturn(paymentDTO);

        PaymentPageDTO page = paymentService.getPage(payments, pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(0);
    }

    @Test
    void softDelete() {
        paymentService.softDelete(payment.getPassengerId());
        verify(paymentRepository).softDelete(payment.getPassengerId());
    }

    @Test
    void hardDelete() {
        paymentService.hardDelete(payment.getPassengerId());
        verify(paymentRepository).deleteByPassengerId(payment.getPassengerId());
    }
}
