package org.example.repositories;

import org.example.entities.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Modifying
    @Query("UPDATE Balance b SET b.balance = b.balance + :deposit, b.timeLastDeposit = CURRENT_TIMESTAMP WHERE b.passengerId = :passengerId")
    int topUpBalance(@Param("passengerId") Long passengerId, @Param("deposit") double deposit);

    @Modifying
    @Query("UPDATE Balance b SET b.deleted = true WHERE b.passengerId = :passengerId")
    void softDelete(@Param("passengerId") Long passengerId);
}
