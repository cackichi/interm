package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.BalanceDTO;
import org.example.entities.Balance;
import org.example.repositories.BalanceRepository;
import org.hibernate.id.IdentifierGenerationException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService{
    private final BalanceRepository balanceRepository;
    private final ModelMapper modelMapper;

    @Override
    public Balance mapToBalance(BalanceDTO balanceDTO){
        return modelMapper.map(balanceDTO, Balance.class);
    }
    @Override
    public BalanceDTO mapToDTO(Balance balance){
        return modelMapper.map(balance, BalanceDTO.class);
    }
    @Override
    @Transactional
    public void topUpBalance(Long id, double deposit) {
        log.info("Topping up balance for passenger {}, amount: {}", id, deposit);
        int countUpdated = balanceRepository.topUpBalance(id, deposit);
        if (countUpdated == 0) {
            log.error("Balance not found for passenger {}", id);
            throw new EntityNotFoundException("Баланс не найден");
        }
    }

    @Override
    public BalanceDTO getBalance(Long id) {
        log.debug("Getting balance for passenger {}", id);
        return balanceRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.error("Balance not found for passenger {}", id);
                    return new EntityNotFoundException("Баланс не найден");
                });
    }

    @Override
    @Transactional
    public void softDelete(Long passengerId) {
        log.info("Soft deleting balance for passenger {}", passengerId);
        balanceRepository.softDelete(passengerId);
    }

    @Override
    @Transactional
    public void hardDelete(Long passengerId) {
        log.info("Hard deleting balance for passenger {}", passengerId);
        balanceRepository.deleteById(passengerId);
    }

    @Override
    public Balance create(BalanceDTO balanceDTO) {
        if (balanceDTO.getPassengerId() == null) {
            log.error("Passenger id is null when creating balance");
            throw new IdentifierGenerationException("Вы не указали идентификатор пассажира");
        }

        log.info("Creating new balance for passenger {}", balanceDTO.getPassengerId());
        return balanceRepository.save(mapToBalance(balanceDTO));
    }
}
