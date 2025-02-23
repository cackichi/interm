package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Balance {
    @Id
    private Long passengerId;
    private double balance;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime timeLastDeposit;
    private boolean deleted = false;
}
