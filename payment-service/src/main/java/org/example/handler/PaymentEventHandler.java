package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.asepcts.KafkaHandler;
import org.example.dto.PaymentDTO;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.services.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentEventHandler {
    private final PaymentService paymentService;

    @KafkaListener(topics = "stop-travel-event-topic")
    @KafkaHandler
    public void createPaymentEvent(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in payment event");
            throw new NonRetryableException("Non retryable passenger id = null");
        }
        PaymentDTO payment = PaymentDTO.builder()
                .cost(travelEvent.getCostOfRide())
                .rideId(travelEvent.getRideId())
                .passengerId(travelEvent.getPassengerId())
                .build();

        log.info("Creating payment for ride {}, passenger {}",
                travelEvent.getRideId(), travelEvent.getPassengerId());
        paymentService.create(payment);
    }
}