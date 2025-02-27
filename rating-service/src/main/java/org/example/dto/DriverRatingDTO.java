package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DriverRatingDTO {
    private Long driverId;
    private double averageRating;
    private int ratingCount;
    private boolean deleted = false;
}
