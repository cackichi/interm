package org.example.integration.util;

import org.example.dto.TravelEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KafkaConsumer {
    private Map<String, TravelEvent> map = new HashMap<>();

    @KafkaListener(topics = "driver-create-event-topic")
    public void handlePassengerCreated(TravelEvent travelEvent) {
        processMessage("driver-create-event-topic", travelEvent);
    }
    @KafkaListener(topics = "driver-soft-delete-event-topic")
    public void handlePassengerSoftDeleted(TravelEvent travelEvent) {
        processMessage("driver-soft-delete-event-topic", travelEvent);
    }
    @KafkaListener(topics = "driver-hard-delete-event-topic")
    public void handlePassengerHardDeleted(TravelEvent travelEvent) {
        processMessage("driver-hard-delete-event-topic", travelEvent);
    }

    @KafkaListener(topics = "driver-valid-event-topic")
    public void handleTaxiOrder(TravelEvent travelEvent) {
        processMessage("driver-valid-event-topic", travelEvent);
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
