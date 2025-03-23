package org.example.integration;

import org.example.dto.TravelEvent;
import org.example.repositories.DriverRatingRepository;
import org.example.services.DriverRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class DriverRatingEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private DriverRatingService driverRatingService;
    @Autowired
    private DriverRatingRepository driverRatingRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @BeforeEach
    void setUp(){
        driverRatingRepository.deleteAll();
    }
    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("110");
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isPresent()).isFalse();
        kafkaTemplate.send("driver-create-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isPresent()).isTrue();
                });
    }
    @Test
    void testSoftDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("110");
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 5);
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).orElseThrow().isDeleted()).isFalse();
        kafkaTemplate.send("driver-soft-delete-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).orElseThrow().isDeleted()).isTrue();
                });
    }
    @Test
    void testHardDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("110");
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 5);
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isEmpty()).isFalse();
        kafkaTemplate.send("driver-hard-delete-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isEmpty()).isTrue();
                });
    }
}
