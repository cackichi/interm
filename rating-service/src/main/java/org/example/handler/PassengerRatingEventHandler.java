package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.services.PassengerRatingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PassengerRatingEventHandler {
    private final PassengerRatingService passengerRatingService;

    @KafkaListener(topics = "passenger-create-event-topic")
    public void createRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in create rating event");
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }
        log.info("Creating initial rating for passenger {}", travelEvent.getPassengerId());
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 0);
    }

    @KafkaListener(topics = "passenger-hard-delete-event-topic")
    public void hardDeleteRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in hard delete rating event");
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }
        log.info("Hard deleting rating for passenger {}", travelEvent.getPassengerId());
        passengerRatingService.hardDelete(travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "passenger-soft-delete-event-topic")
    public void softDeleteRatingEvent(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in soft delete rating event");
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }

        log.info("Soft deleting rating for passenger {}", travelEvent.getPassengerId());
        passengerRatingService.softDelete(travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "stop-travel-event-topic")
    public void stopTravelEvent(TravelEvent travelEvent) {
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in stop travel event");
            throw new NonRetryableException("Non retryable exception - passenger id is null");
        }

        log.info("Updating rating for passenger {} with value {}",
                travelEvent.getPassengerId(), travelEvent.getRatingForPassenger());
        passengerRatingService.updateOrSaveRating(
                travelEvent.getPassengerId(),
                travelEvent.getRatingForPassenger()
        );
    }
}