package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.RideDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.services.RideService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RideEventHandler {
    private final RideService rideService;

    @KafkaListener(topics = "driver-valid-event-topic")
    public void handleAttachRide(TravelEvent travelEvent){
        if(travelEvent.getRideId() == null || travelEvent.getDriverId() == null)
            throw new NonRetryableException("Non retryable exception - driver or ride id is null");
        try{
            rideService.attachDriver(travelEvent.getDriverId(), travelEvent.getRideId());
        } catch (Exception e){
            throw new NonRetryableException(e);
        }
    }

    @KafkaListener(topics = "order-taxi-event-topic")
    public void createRideAfterPassengerReq(TravelEvent travelEvent){
        RideDTO ride = RideDTO.builder()
                .passengerId(travelEvent.getPassengerId())
                .pointA(travelEvent.getPointA())
                .pointB(travelEvent.getPointB())
                .build();
        rideService.create(ride);
    }
}
