package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.exceptions.NotFoundException;
import org.example.services.DriverServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DriverEventHandler {
    private final DriverServiceImpl driverServiceImpl;

    @KafkaListener(topics = "stop-travel-event-topic")
    public void handleStopTravel(TravelEvent travelEvent){
        if(travelEvent.getDriverId() == null) throw new NonRetryableException("Non retryable exception - driver id is null");
        try {
            driverServiceImpl.updateStatus(travelEvent.getDriverId(), "FREE");
        } catch (NotFoundException e) {
            throw new NonRetryableException("Non retryable exception - driver not found");
        }
    }

    @KafkaListener(topics = "check-driver-event-topic")
    public void handleStartTravel(TravelEvent travelEvent){
        if(travelEvent.getDriverId() == null) throw new NonRetryableException("Non retryable exception - passegner id is null");
        driverServiceImpl.driverValidEvent(travelEvent.getDriverId(), travelEvent.getRideId());
    }
}
