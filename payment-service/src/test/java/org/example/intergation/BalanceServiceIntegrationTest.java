package org.example.intergation;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.BalanceDTO;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BalanceServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;

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
