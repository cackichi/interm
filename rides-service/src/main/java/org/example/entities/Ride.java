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
    private Long passengerId;
    private Long driverId;
    private String pointA;
    private String pointB;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    private boolean deleted = false;
}
