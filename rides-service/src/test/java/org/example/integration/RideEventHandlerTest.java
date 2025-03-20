package org.example.integration;

import org.example.dto.RideDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Status;
import org.example.repositories.RideRepository;
import org.example.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class RideEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private RideService rideService;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
    }

    @Test
    void testHandleAttachRide() {
        RideDTO testRide = rideService.create(new RideDTO(
                1L,
                2L,
                "1",
                "Point A",
                "Point B",
                Status.WAITING,
                false
        ));
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("100");
        travelEvent.setRideId(testRide.getId());

        kafkaTemplate.send("driver-valid-event-topic", testRide.getDriverId(), travelEvent);
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    RideDTO rideDTO = rideService.findById(testRide.getId());
                    assertThat(rideDTO.getStatus()).isEqualTo(Status.TRAVELING);
                });
    }

    @Test
    void testCreateRideAfterPassengerReq() {
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(1L);
        travelEvent.setPointA("Moskow");
        travelEvent.setPointB("Vitebsk");
        kafkaTemplate.send("order-taxi-event-topic", "33", travelEvent);
        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(rideRepository.findAll()).hasSize(1);
                });
    }
}
