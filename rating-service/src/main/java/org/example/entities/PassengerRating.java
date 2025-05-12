package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "passenger_rating")
public class PassengerRating {
    @Id
    private Long passengerId;
    private double averageRating;
    private int ratingCount;
    private boolean deleted = false;
}
