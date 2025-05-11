package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.asepcts.KafkaHandler;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.exceptions.NotFoundException;
import org.example.services.DriverServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class DriverEventHandler {
    private final DriverServiceImpl driverServiceImpl;

    @KafkaListener(topics = "stop-travel-event-topic")
    @KafkaHandler
    public void handleStopTravel(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if(travelEvent.getDriverId() == null) {
            log.error("Driver ID is null in stop travel event");
            throw new NonRetryableException("Non retryable exception - driver id is null");
        }

        try {
            driverServiceImpl.updateStatus(travelEvent.getDriverId(), "FREE");
            log.debug("Driver {} status updated to FREE", travelEvent.getDriverId());
        } catch (NotFoundException e) {
            log.error("Driver not found: {}", e.getMessage());
            throw new NonRetryableException("Non retryable exception - driver not found");
        }
    }

    @KafkaListener(topics = "check-driver-event-topic")
    @KafkaHandler
    public void handleStartTravel(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        log.info("Processing check driver event: {}", travelEvent);

        if(travelEvent.getDriverId() == null) {
            log.error("Driver ID is null in check driver event");
            throw new NonRetryableException("Non retryable exception - driver id is null");
        }
            driverServiceImpl.driverValidEvent(travelEvent.getDriverId(), travelEvent.getRideId());
            log.debug("Driver {} validation processed", travelEvent.getDriverId());
    }
}
