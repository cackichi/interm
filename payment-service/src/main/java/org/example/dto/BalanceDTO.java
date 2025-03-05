package org.example.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BalanceDTO {
    private Long passengerId;
    private double balance;
    private LocalDateTime timeLastDeposit;
    private boolean deleted = false;
}
