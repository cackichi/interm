package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Balance {
    @Id
    private Long passengerId;
    private double balance;
    @Column(columnDefinition = "timestamp")
    private LocalDateTime timeLastDeposit;
    private boolean deleted = false;
}
