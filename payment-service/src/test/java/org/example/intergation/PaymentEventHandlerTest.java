package org.example.intergation;

import org.example.dto.TravelEvent;
import org.example.entities.Payment;
import org.example.repositories.PaymentRepository;
import org.example.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class PaymentEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @BeforeEach
    void setUp(){
        paymentRepository.deleteAll();
    }

    @Test
    void testCreatePaymentEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
        travelEvent.setCostOfRide(300);
        travelEvent.setRideId(100L);
        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(10, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    List<Payment> payments = paymentRepository.findAll();
                    assertThat(payments).hasSize(1);
                    Payment payment = payments.get(0);
                    assertThat(payment.getCost()).isEqualTo(travelEvent.getCostOfRide());
                    assertThat(payment.getRideId()).isEqualTo(travelEvent.getRideId());
                });
    }
}
