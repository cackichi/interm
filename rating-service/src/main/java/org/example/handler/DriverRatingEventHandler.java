package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.services.DriverRatingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class DriverRatingEventHandler {
    private final DriverRatingService driverRatingService;

    @KafkaListener(topics = "driver-create-event-topic")
    public void createRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getDriverId() == null) {
            log.error("Driver id is null in create rating event");
            throw new NonRetryableException("Non retryable exception - driver id is null");
        }
        log.info("Creating initial rating for driver {}", travelEvent.getDriverId());
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 0);
    }

    @KafkaListener(topics = "driver-hard-delete-event-topic")
    public void hardDeleteRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getDriverId() == null) {
            log.error("Driver id is null in hard delete rating event");
            throw new NonRetryableException("Non retryable exception - driver id is null");
        }
        log.info("Hard deleting rating for driver {}", travelEvent.getDriverId());
        driverRatingService.hardDelete(travelEvent.getDriverId());
    }

    @KafkaListener(topics = "driver-soft-delete-event-topic")
    public void softDeleteRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getDriverId() == null) {
            log.error("Driver id is null in soft delete rating event");
            throw new NonRetryableException("Non retryable exception - driver id is null");
        }

        log.info("Soft deleting rating for driver {}", travelEvent.getDriverId());
        driverRatingService.softDelete(travelEvent.getDriverId());
    }
}