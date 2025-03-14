package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_passenger_id")
    @SequenceGenerator(sequenceName = "seq_passenger_id", name = "seq_passenger_id", allocationSize = 1, initialValue = 100)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean deleted = false;
    @Enumerated(EnumType.STRING)
    private Status status;
}
