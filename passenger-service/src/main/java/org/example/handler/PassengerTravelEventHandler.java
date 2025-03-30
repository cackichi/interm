package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TravelEvent;
import org.example.entities.Status;
import org.example.exception.NonRetryableException;
import org.example.services.PassengerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PassengerTravelEventHandler {
    private final PassengerService passengerService;

    @KafkaListener(topics = "stop-travel-event-topic")
    public void handleStopTravel(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }
        log.info("Processing stop travel event. Passenger ID: {}", travelEvent.getPassengerId());
        passengerService.travelEventUpdate(Status.NOT_ACTIVE, travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "start-travel-event-topic")
    public void handleStartTravel(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }
        log.info("Processing stop travel event. Passenger ID: {}", travelEvent.getPassengerId());
        passengerService.travelEventUpdate(Status.TRAVELING, travelEvent.getPassengerId());
    }
}
