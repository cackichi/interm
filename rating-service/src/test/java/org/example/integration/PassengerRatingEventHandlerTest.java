package org.example.integration;

import org.example.dto.PassengerRatingDTO;
import org.example.dto.TravelEvent;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class PassengerRatingEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private PassengerRatingService passengerRatingService;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Autowired
    private PassengerRatingRepository passengerRatingRepository;
    @BeforeEach
    void setUp(){
        passengerRatingRepository.deleteAll();
    }
    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(130L);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isPresent()).isFalse();
        kafkaTemplate.send("passenger-create-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isPresent()).isTrue();
                });
    }
    @Test
    void testSoftDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(130L);
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 5);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).orElseThrow().isDeleted()).isFalse();
        kafkaTemplate.send("passenger-soft-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).orElseThrow().isDeleted()).isTrue();
                });
    }
    @Test
    void testHardDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(130L);
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 5);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isEmpty()).isFalse();
        kafkaTemplate.send("passenger-hard-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isEmpty()).isTrue();
                });
    }
    @Test
    void testStopTravelEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(130L);
        travelEvent.setRatingForPassenger(5);
        PassengerRatingDTO passengerRatingDTO = new PassengerRatingDTO(
                travelEvent.getPassengerId(),
                4.5,
                3,
                false
        );
        passengerRatingService.create(passengerRatingDTO);
        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).get().getAverageRating())
                            .isEqualTo(4.625);
                });

        travelEvent.setPassengerId(101L);
        travelEvent.setRatingForPassenger(3.45);
        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);
        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    if(!passengerRatingRepository.findAll().isEmpty()) {
                        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).get().getAverageRating())
                                .isEqualTo(travelEvent.getRatingForPassenger());
                    }
                });
    }
}
