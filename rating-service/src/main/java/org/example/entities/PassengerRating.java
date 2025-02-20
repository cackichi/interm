package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "passenger_rating")
public class PassengerRating {
    @Id
    private Long passengerId;
    private double averageRating;
    private int ratingCount;
    private boolean deleted = false;
}
