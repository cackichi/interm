package org.example.integration;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.integration.util.KafkaConsumer;
import org.example.repositories.PassengerRepo;
import org.example.services.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private PassengerRepo passengerRepo;
    @Autowired
    private KafkaConsumer kafkaConsumer;
    @Autowired
    private PassengerServiceImpl passengerService;


    @BeforeEach
    void setUp() {
        passengerRepo.deleteAll();
    }

    @Test
    void testSavePassengerAndCreateTopic() {
        PassengerDTO passengerDTO = new PassengerDTO(
                100L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPassenger = passengerService.save(passengerDTO);
        assertNotNull(savedPassenger);
        assertThat(passengerDTO.getEmail()).isEqualTo(savedPassenger.getEmail());

        await().atMost(3, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("passenger-create-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("passenger-create-event-topic").get();
        kafkaConsumer.clear();
        assertEquals(travelEvent.getPassengerId(), savedPassenger.getId());
    }

    @Test
    void testUpdatePassenger() {
        PassengerDTO passengerDTO = new PassengerDTO(
                2L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPassenger = passengerService.save(passengerDTO);

        PassengerDTO updatedDTO = new PassengerDTO(
                2L,
                "Петр Иванов",
                "petr@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        passengerService.updatePass(savedPassenger.getId(), updatedDTO);

        PassengerDTO updated = passengerService.findOne(savedPassenger.getId());

        assertEquals(updatedDTO.getName(), updated.getName());
    }

    @Test
    void testSoftDeleteAndSoftTopic() {
        PassengerDTO passengerDTO = new PassengerDTO(
                1L,
                "Петр Иванов",
                "petr@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPassenger = passengerService.save(passengerDTO);

        passengerService.softDelete(savedPassenger.getId());

        assertTrue(passengerRepo.findById(savedPassenger.getId()).isPresent());
        assertTrue(passengerRepo.findById(savedPassenger.getId()).get().isDeleted());

        await().atMost(10, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("passenger-soft-delete-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("passenger-soft-delete-event-topic").get();
        kafkaConsumer.clear();
        assertEquals(travelEvent.getPassengerId(), savedPassenger.getId());
    }

    @Test
    void testSoftDeleteAndHardTopic() {
        PassengerDTO passengerDTO = new PassengerDTO(
                1L,
                "Петр Иванов",
                "petr@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPassenger = passengerService.save(passengerDTO);

        passengerService.hardDelete(savedPassenger.getId());

        assertThrows(EntityNotFoundException.class, () -> passengerService.findOne(savedPassenger.getId()));

        await().atMost(3, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("passenger-hard-delete-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("passenger-hard-delete-event-topic").get();
        kafkaConsumer.clear();
        assertEquals(travelEvent.getPassengerId(), savedPassenger.getId());
    }

    @Test
    void testFindAllNotDeleted() {
        PassengerDTO passengerDTO1 = new PassengerDTO(
                1L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        PassengerDTO passengerDTO2 = new PassengerDTO(
                2L,
                "Петр Иванов",
                "petr@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        passengerService.save(passengerDTO1);
        passengerService.save(passengerDTO2);

        Pageable pageable = PageRequest.of(0, 10);
        PassengerPageDTO page = passengerService.findAllNotDeleted(pageable);

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getTotalPages());
    }

    @Test
    void testCheckExistsAndStatus() {
        PassengerDTO passengerDTO = new PassengerDTO(
                2L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPassenger = passengerService.save(passengerDTO);

        assertTrue(passengerService.checkExistsAndStatus(savedPassenger.getId()));

        passengerService.travelEventUpdate(Status.WAITING, savedPassenger.getId());

        assertFalse(passengerService.checkExistsAndStatus(savedPassenger.getId()));
    }

    @Test
    void findOne() {
        PassengerDTO passengerDTO = new PassengerDTO(
                2L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        Passenger savedPass = passengerService.save(passengerDTO);

        PassengerDTO findPass = passengerService.findOne(savedPass.getId());

        assertThat(findPass)
                .isNotNull()
                .isEqualTo(passengerService.mapToDTO(savedPass));

        assertThrows(EntityNotFoundException.class, () ->
                passengerService.findOne(2L));
    }

    @Test
    void checkOrderTaxiTopic(){
        PassengerDTO passengerDTO = new PassengerDTO(
                2L,
                "Иван Петров",
                "ivan@example.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        Passenger savedPass = passengerService.save(passengerDTO);

        passengerService.orderTaxi(savedPass.getId(), "Moskow", "Mogilev");

        await().atMost(3, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("order-taxi-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("order-taxi-event-topic").get();
        kafkaConsumer.clear();
        assertEquals(travelEvent.getPassengerId(), savedPass.getId());
        assertEquals(travelEvent.getPointA(), "Moskow");
        assertEquals(travelEvent.getPointB(), "Mogilev");

        PassengerDTO changedPassenger = passengerService.findOne(savedPass.getId());
        assertEquals(Status.WAITING, changedPassenger.getStatus());
    }
}
