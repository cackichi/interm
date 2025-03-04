package org.example.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TravelEvent {
    private Long passengerId;
    private double costOfRide;
    private double ratingForPassenger;
    private Long rideId;
    private String driverId;
}
