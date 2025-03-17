package org.example.intergation;

import org.example.dto.BalanceDTO;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Balance;
import org.example.entities.Payment;
import org.example.entities.Status;
import org.example.exceptions.InsufficientBalanceException;
import org.example.repositories.PaymentRepository;
import org.example.services.BalanceService;
import org.example.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
@Testcontainers
public class PaymentServiceIntegrationTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BalanceService balanceService;
    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

    @BeforeEach
    void setUp(){
        paymentRepository.deleteAll();
    }

    @Test
    void testCreate(){
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );

        Payment savedPayment = paymentService.create(paymentDTO);

        assertThat(savedPayment.getId()).isNotEqualTo(paymentDTO.getId());
        assertThat(savedPayment.getRideId()).isEqualTo(paymentDTO.getRideId());
        assertThat(savedPayment.getPassengerId()).isEqualTo(paymentDTO.getPassengerId());
        assertThat(savedPayment.getCost()).isEqualTo(paymentDTO.getCost());
    }

    @Test
    void testClosePayment() throws InsufficientBalanceException {
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );
        Payment savedPayment = paymentService.create(paymentDTO);
        Balance savedBalance = balanceService.create(new BalanceDTO(
                paymentDTO.getPassengerId(),
                1000,
                LocalDateTime.now(),
                false
        ));
        paymentService.closePayment(savedPayment.getPassengerId());

        assertThat(paymentService
                .getPaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10))
                .getPayments()
                .get(0)
                .getStatus()).isEqualTo(Status.PAID);

        assertThat(balanceService.getBalance(savedBalance.getPassengerId()).getBalance())
                .isEqualTo(savedBalance.getBalance() - savedPayment.getCost());
    }
    @Test
    void testClosePaymentWithNotEnoughBalance(){
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );
        paymentService.create(paymentDTO);
        balanceService.create(new BalanceDTO(
                paymentDTO.getPassengerId(),
                110,
                LocalDateTime.now(),
                false
        ));

        assertThrows(InsufficientBalanceException.class, () -> paymentService.closePayment(paymentDTO.getPassengerId()));
    }
    @Test
    void testPaidUnpaid(){
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );
        paymentService.create(paymentDTO);
        PaymentPageDTO unpaid = paymentService.getUnpaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10));
        assertThat(unpaid.getPayments()).hasSize(1);
        assertThat(unpaid.getTotalPages()).isEqualTo(1);
        assertThat(unpaid.getTotalElements()).isEqualTo(1);

        paymentDTO.setStatus(Status.PAID);
        paymentRepository.save(paymentService.mapToPayment(paymentDTO));
        PaymentPageDTO paid = paymentService.getPaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10));
        assertThat(paid.getPayments()).hasSize(1);
        assertThat(paid.getTotalPages()).isEqualTo(1);
        assertThat(paid.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testSoftDelete(){
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );
        Payment savedPayment = paymentService.create(paymentDTO);
        paymentService.softDelete(savedPayment.getPassengerId());

        assertThat(paymentRepository.findById(savedPayment.getId()).orElseThrow().isDeleted())
                .isTrue();
    }

    @Test
    void testHardDelete(){
        PaymentDTO paymentDTO = new PaymentDTO(
                33L,
                100L,
                10L,
                150,
                Status.WAITING,
                false
        );
        Payment savedPayment = paymentService.create(paymentDTO);
        paymentService.hardDelete(savedPayment.getId());

        assertThat(paymentRepository.findById(savedPayment.getId()).isEmpty())
                .isTrue();
    }
}
