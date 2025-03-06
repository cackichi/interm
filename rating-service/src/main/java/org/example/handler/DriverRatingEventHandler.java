package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.services.DriverRatingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DriverRatingEventHandler {
    private final DriverRatingService driverRatingService;
    @KafkaListener(topics = "driver-create-event-topic")
    public void createRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getDriverId() == null) throw new NonRetryableException("Non retryable exception - driver id is null");
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 0);
    }
    @KafkaListener(topics = "driver-hard-delete-event-topic")
    public void hardDeleteRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getDriverId() == null) throw new NonRetryableException("Non retryable exception - driver id is null");
        driverRatingService.hardDelete(travelEvent.getDriverId());
    }
    @KafkaListener(topics = "driver-soft-delete-event-topic")
    public void softDeleteRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getDriverId() == null) throw new NonRetryableException("Non retryable exception - driver id is null");
        driverRatingService.softDelete(travelEvent.getDriverId());
    }
}
