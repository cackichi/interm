package org.example.integration.util;

import org.example.dto.TravelEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KafkaConsumer {
    private Map<String, TravelEvent> map = new HashMap<>();

    @KafkaListener(topics = "passenger-create-event-topic")
    public void handlePassengerCreated(TravelEvent travelEvent) {
        processMessage("passenger-create-event-topic", travelEvent);
    }
    @KafkaListener(topics = "passenger-soft-delete-event-topic")
    public void handlePassengerSoftDeleted(TravelEvent travelEvent) {
        processMessage("passenger-soft-delete-event-topic", travelEvent);
    }
    @KafkaListener(topics = "passenger-hard-delete-event-topic")
    public void handlePassengerHardDeleted(TravelEvent travelEvent) {
        processMessage("passenger-hard-delete-event-topic", travelEvent);
    }

    @KafkaListener(topics = "order-taxi-event-topic")
    public void handleTaxiOrder(TravelEvent travelEvent) {
        processMessage("order-taxi-event-topic", travelEvent);
    }

    private void processMessage(String topic, TravelEvent travelEvent) {
        this.map.put(topic, travelEvent);
    }

    public void clear(){
        this.map = new HashMap<>();
    }

    public Optional<TravelEvent> getProcessedMessages(String topic) {
        return Optional.of(map.get(topic));
    }
}
