package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TravelEvent {
    private Long passengerId;
    private double costOfRide;
    private double ratingForPassenger;
    private Long rideId;
}
