package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.asepcts.KafkaHandler;
import org.example.dto.RideDTO;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.services.RideService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class RideEventHandler {
    private final RideService rideService;

    @KafkaListener(topics = "driver-valid-event-topic")
    @KafkaHandler
    public void handleAttachRide(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if (travelEvent.getRideId() == null || travelEvent.getDriverId() == null) {
            log.error("Driver or ride id is null in travel event");
            throw new NonRetryableException("Non retryable exception - driver or ride id is null");
        }

        try {
            log.info("Attaching driver {} to ride {}", travelEvent.getDriverId(), travelEvent.getRideId());
            rideService.attachDriver(travelEvent.getDriverId(), travelEvent.getRideId());
        } catch (Exception e) {
            log.error("Failed to attach driver to ride: {}", e.getMessage());
            throw new NonRetryableException(e);
        }
    }

    @KafkaListener(topics = "order-taxi-event-topic")
    @KafkaHandler
    public void createRideAfterPassengerReq(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        RideDTO ride = RideDTO.builder()
                .passengerId(travelEvent.getPassengerId())
                .pointA(travelEvent.getPointA())
                .pointB(travelEvent.getPointB())
                .build();

        log.info("Creating new ride for passenger {}", travelEvent.getPassengerId());
        rideService.create(ride);
    }
}