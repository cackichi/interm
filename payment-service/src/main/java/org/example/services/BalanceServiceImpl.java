package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.BalanceDTO;
import org.example.entities.Balance;
import org.example.repositories.BalanceRepository;
import org.hibernate.id.IdentifierGenerationException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
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
    public void topUpBalance(Long id, double deposit){
        int countUpdated = balanceRepository.topUpBalance(id, deposit);
        if(countUpdated == 0) throw new EntityNotFoundException("Баланс не найден");
    }
    @Override
    public BalanceDTO getBalance(Long id){
        return balanceRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new EntityNotFoundException("Баланс не найден"));
    }
    @Override
    @Transactional
    public void softDelete(Long passengerId){
        balanceRepository.softDelete(passengerId);
    }
    @Override
    @Transactional
    public void hardDelete(Long passengerId){
        balanceRepository.deleteById(passengerId);
    }
    @Override
    public void create(BalanceDTO balanceDTO){
        if(balanceDTO.getPassengerId() == null) throw new IdentifierGenerationException("Вы не указали идентификатор пассажира");
        balanceRepository.save(mapToBalance(balanceDTO));
    }
}
