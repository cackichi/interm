package org.example.intergation;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.BalanceDTO;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
@Testcontainers
public class BalanceServiceIntegrationTest {
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;
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
        balanceRepository.deleteAll();
    }

    @Test
    void testCreate(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setTimeLastDeposit(LocalDateTime.now());
        balanceDTO.setDeleted(false);

        balanceService.create(balanceDTO);
        BalanceDTO res = balanceService.getBalance(balanceDTO.getPassengerId());
        assertThat(res.getBalance()).isEqualTo(balanceDTO.getBalance());
        assertThat(res.getTimeLastDeposit()).isEqualToIgnoringNanos(balanceDTO.getTimeLastDeposit());
        assertThat(res.isDeleted()).isEqualTo(balanceDTO.isDeleted());
    }
    @Test
    void testGetNotExistsBalance(){
        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(999L));
    }
    @Test
    void testTopUpBalance(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setTimeLastDeposit(LocalDateTime.now());
        balanceDTO.setDeleted(false);

        balanceService.create(balanceDTO);
        balanceService.topUpBalance(balanceDTO.getPassengerId(), 150);

        assertThat(balanceService.getBalance(balanceDTO.getPassengerId()).getBalance())
                .isEqualTo(balanceDTO.getBalance() + 150);
    }
    @Test
    void testTopUpNotExistBalance(){
        assertThrows(EntityNotFoundException.class, () -> balanceService.topUpBalance(999L, 100));
    }
    @Test
    void testSoftDelete(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setTimeLastDeposit(LocalDateTime.now());
        balanceDTO.setDeleted(false);

        balanceService.create(balanceDTO);
        balanceService.softDelete(balanceDTO.getPassengerId());

        assertThat(balanceService.getBalance(balanceDTO.getPassengerId()).isDeleted())
                .isTrue();
    }
    @Test
    void testHardDelete(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setTimeLastDeposit(LocalDateTime.now());
        balanceDTO.setDeleted(false);

        balanceService.create(balanceDTO);
        balanceService.hardDelete(balanceDTO.getPassengerId());

        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(balanceDTO.getPassengerId()));
    }
}
