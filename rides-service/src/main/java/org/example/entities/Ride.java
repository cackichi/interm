package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ride_seq")
    @SequenceGenerator(name = "ride_seq", sequenceName = "ride_seq", initialValue = 100, allocationSize = 1)
    private Long id;
    private Long passengerId;
    private String driverId;
    @Column(name = "point_a")
    private String pointA;
    @Column(name = "point_b")
    private String pointB;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean deleted = false;
}
