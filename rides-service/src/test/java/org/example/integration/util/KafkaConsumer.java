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

    @KafkaListener(topics = "check-driver-event-topic")
    public void handleCheckDriver(TravelEvent travelEvent) {
        processMessage("check-driver-event-topic", travelEvent);
    }
    @KafkaListener(topics = "stop-travel-event-topic")
    public void handleStopTravel(TravelEvent travelEvent) {
        processMessage("stop-travel-event-topic", travelEvent);
    }

    private void processMessage(String topic, TravelEvent travelEvent) {
        this.map.put(topic, travelEvent);
    }

    public Optional<TravelEvent> getProcessedMessages(String topic) {
        return Optional.ofNullable(map.get(topic));
    }
}
