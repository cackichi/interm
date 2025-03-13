package org.example.unit.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.BalanceDTO;
import org.example.entities.Balance;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceServiceImpl;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BalanceRepository balanceRepository;
    @InjectMocks
    private BalanceServiceImpl balanceService;
    private Balance balance;
    private BalanceDTO balanceDTO;

    @BeforeEach
    void setUp(){
        balance = new Balance(
          1L,
          1000,
          LocalDateTime.now(),
          false
        );
        balanceDTO = new BalanceDTO(
                1L,
                1000,
                LocalDateTime.now(),
                false
        );
    }

    @Test
    void mapToBalance(){
        when(modelMapper.map(balanceDTO, Balance.class))
                .thenReturn(balance);

        Balance res = balanceService.mapToBalance(balanceDTO);

        assertThat(res)
                .isNotNull()
                .isEqualTo(balance);

        verify(modelMapper).map(balanceDTO, Balance.class);
    }

    @Test
    void mapToDTO(){
        when(modelMapper.map(balance, BalanceDTO.class))
                .thenReturn(balanceDTO);

        BalanceDTO res = balanceService.mapToDTO(balance);

        assertThat(res)
                .isNotNull()
                .isEqualTo(balanceDTO);

        verify(modelMapper).map(balance, BalanceDTO.class);
    }

    @Test
    void topUpBalance(){
        when(balanceRepository.topUpBalance(balance.getPassengerId(), 100))
                .thenReturn(1);
        balanceService.topUpBalance(balance.getPassengerId(), 100);

        when(balanceRepository.topUpBalance(balance.getPassengerId(), 100))
                .thenReturn(0);
        assertThrows(EntityNotFoundException.class, () -> balanceService.topUpBalance(balance.getPassengerId(), 100));

        verify(balanceRepository, times(2)).topUpBalance(balance.getPassengerId(), 100);
    }

    @Test
    void getBalance(){
        when(modelMapper.map(balance, BalanceDTO.class))
                .thenReturn(balanceDTO);
        when(balanceRepository.findById(balance.getPassengerId()))
                .thenReturn(Optional.of(balance));
        BalanceDTO res = balanceService.getBalance(balance.getPassengerId());
        assertThat(res)
                .isNotNull()
                .isEqualTo(balanceDTO);

        when(balanceRepository.findById(balance.getPassengerId()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> balanceService.getBalance(balance.getPassengerId()));

        verify(balanceRepository, times(2)).findById(balance.getPassengerId());
    }

    @Test
    void softDelete(){
        balanceService.softDelete(balance.getPassengerId());
        verify(balanceRepository).softDelete(balance.getPassengerId());
    }

    @Test
    void hardDelete(){
        balanceService.hardDelete(balance.getPassengerId());
        verify(balanceRepository).deleteById(balance.getPassengerId());
    }

    @Test
    void create(){
        when(modelMapper.map(balanceDTO, Balance.class))
                .thenReturn(balance);

        balanceService.create(balanceDTO);
        assertThrows(IdentifierGenerationException.class, () -> balanceService.create(new BalanceDTO()));

        verify(balanceRepository, times(1)).save(balance);
    }
}
