package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_seq", initialValue = 100, allocationSize = 1)
    private Long id;
    private Long passengerId;
    private Long rideId;
    private double cost;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean deleted = false;
}
