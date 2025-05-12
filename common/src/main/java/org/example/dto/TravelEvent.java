package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class TravelEvent {
    private Long passengerId;
    private double costOfRide;
    private double ratingForPassenger;
    private Long rideId;
    private String driverId;
    private String pointA;
    private String pointB;

    public TravelEvent(String driverId) {
        this.driverId = driverId;
    }
}