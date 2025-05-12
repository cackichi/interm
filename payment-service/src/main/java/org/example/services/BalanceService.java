package org.example.services;

import org.example.dto.BalanceDTO;
import org.example.entities.Balance;

public interface BalanceService {
    Balance mapToBalance(BalanceDTO balanceDTO);

    BalanceDTO mapToDTO(Balance balance);

    void topUpBalance(Long id, double deposit);

    BalanceDTO getBalance(Long id);

    void softDelete(Long passengerId);

    void hardDelete(Long passengerId);

    Balance create(BalanceDTO balanceDTO);
}
