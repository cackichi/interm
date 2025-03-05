package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.PaymentDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.services.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PaymentEventHandler {
    private final PaymentService paymentService;

    @KafkaListener(topics = "stop-travel-event-topic")
    public void createPaymentEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable passenger id = null");
        PaymentDTO payment = PaymentDTO.builder()
                .cost(travelEvent.getCostOfRide())
                .rideId(travelEvent.getRideId())
                .passengerId(travelEvent.getPassengerId())
                .build();
        paymentService.create(payment);
    }
}
