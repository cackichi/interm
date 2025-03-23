package org.example.integration;

import org.example.dto.PassengerDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.repositories.PassengerRepo;
import org.example.services.PassengerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class PassengerTravelEventHandlerTest extends BaseIntegrationTest{

    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private PassengerRepo passengerRepo;

    private TravelEvent testEvent = new TravelEvent();

    @BeforeEach
    void setUp() {
        passengerRepo.deleteAll();
    }

    @Test
    void testHandleStopTravel() {
        PassengerDTO passenger = new PassengerDTO(
                1L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        Passenger savedPass = passengerService.save(passenger);
        testEvent.setPassengerId(savedPass.getId());

        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(savedPass.getId()),testEvent);

        await().atMost(10, java.util.concurrent.TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    PassengerDTO updatedPassenger = passengerService.findOne(savedPass.getId());
                    assertThat(updatedPassenger.getStatus()).isEqualTo(Status.NOT_ACTIVE);
                });
    }

    @Test
    void testHandleStartTravel() {
        PassengerDTO passenger = new PassengerDTO(
                1L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        Passenger savedPass = passengerService.save(passenger);
        testEvent.setPassengerId(savedPass.getId());

        kafkaTemplate.send("start-travel-event-topic", testEvent);

        await().atMost(10, java.util.concurrent.TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    PassengerDTO updatedPassenger = passengerService.findOne(savedPass.getId());
                    assertThat(updatedPassenger.getStatus()).isEqualTo(Status.TRAVELING);
                });
    }

    @AfterAll
    static void tearDown(){
        kafka.stop();
        database.stop();
    }
}
