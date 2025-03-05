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
    private String driverId;
    private String pointA;
    private String pointB;
}
