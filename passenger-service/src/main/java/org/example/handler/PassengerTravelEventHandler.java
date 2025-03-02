package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.TravelEvent;
import org.example.entities.Status;
import org.example.exceptions.NonRetryableException;
import org.example.services.PassengerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PassengerTravelEventHandler {
    private final PassengerService passengerService;

    @KafkaListener(topics = "stop-travel-event-topic")
    public void handleStopTravel(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passegner id is null");
        passengerService.travelEventUpdate(Status.TRAVELING, travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "start-travel-event-topic")
    public void handleStartTravel(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable exception - passegner id is null");
        passengerService.travelEventUpdate(Status.NOT_ACTIVE, travelEvent.getPassengerId());
    }
}
