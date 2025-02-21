package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BalanceDTO {
    private Long passengerId;
    private double balance;
    private LocalDate timeLastDeposit;
    private boolean deleted = false;
}
