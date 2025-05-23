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
@Builder
@EqualsAndHashCode
@Table(name = "driver_rating")
public class DriverRating {
    @Id
    private String driverId;
    private double averageRating;
    private int ratingCount;
    private boolean deleted = false;
}
