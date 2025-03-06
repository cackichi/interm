package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.services.PassengerRatingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PassengerRatingEventHandler {
    private final PassengerRatingService passengerRatingService;

    @KafkaListener(topics = "passenger-create-event-topic")
    public void createRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passenger id is null");
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 0);
    }
    @KafkaListener(topics = "passenger-hard-delete-event-topic")
    public void hardDeleteRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passenger id is null");
        passengerRatingService.hardDelete(travelEvent.getPassengerId());
    }
    @KafkaListener(topics = "passenger-soft-delete-event-topic")
    public void softDeleteRatingEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passenger id is null");
        passengerRatingService.softDelete(travelEvent.getPassengerId());
    }
    @KafkaListener(topics = "stop-travel-event-topic")
    public void stopTravelEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passenger id is null");
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), travelEvent.getRatingForPassenger());
    }
}
