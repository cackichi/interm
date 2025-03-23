package org.example.integration;

import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.BusyDriverException;
import org.example.exceptions.NotFoundException;
import org.example.integration.util.KafkaConsumer;
import org.example.repositories.DriverRepository;
import org.example.services.DriverService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DriverServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private KafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        driverRepository.deleteAll();
    }

    @Test
    void testSaveDriverAndCreateEvent() throws NotFoundException {
        kafkaConsumer.clear();
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);

        DriverDTO savedDriver = driverService.findById(driverDTO.getId());

        assertThat(savedDriver).isNotNull().isEqualTo(driverDTO);
        assertThat(savedDriver.getCars().get(0)).isEqualTo(driverDTO.getCars().get(0));

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("driver-create-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("driver-create-event-topic").get();
        assertThat(travelEvent.getDriverId()).isEqualTo(savedDriver.getId());
    }

    @Test
    void testSaveWithNullId(){
        Driver driver = new Driver();
        driver.setName("Radrigo");

        Driver savedDriver = driverRepository.save(driver);

        assertThat(savedDriver.getId()).isNotNull();
    }

    @Test
    void testSoftEvent() throws NotFoundException {
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        driverService.softDelete(driverDTO.getId());

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("driver-soft-delete-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("driver-soft-delete-event-topic").get();
        assertThat(travelEvent.getDriverId()).isEqualTo(driverDTO.getId());
    }

    @Test
    void testUpdateDriver() throws NotFoundException {
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        driverDTO.setName("James");
        driverService.update(driverDTO.getId(), driverDTO);
        DriverDTO updatedDriver = driverService.findById(driverDTO.getId());

        assertThat(updatedDriver).isNotNull().isEqualTo(driverDTO);
    }

    @Test
    void testUpdateStatusForTravel() throws BusyDriverException, NotFoundException {
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        driverService.updateStatusForTravel(driverDTO.getId(), "BUSY");
        DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
        driverDTO.setStatus("BUSY");
        assertThat(updatedDriver).isNotNull().isEqualTo(driverDTO);
    }

    @Test
    void testUpdateStatusForTravelWithBusyStatus() throws BusyDriverException, NotFoundException {
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "BUSY",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        driverService.updateStatus(driverDTO.getId(), "BUSY");
        assertThrows(BusyDriverException.class, () -> driverService.updateStatusForTravel(driverDTO.getId(), "BUSY"));
    }

    @Test
    void testFindAllNotDeleted(){
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "BUSY",
                List.of(new Car("7458", "AUDI", "white", false))
        );
        driverService.create(driverDTO);

        DriverPageDTO driverPageDTO = driverService.findAllNotDeleted(PageRequest.of(0, 10));

        assertThat(driverPageDTO.getDrivers().get(0).getEmail()).isEqualTo(driverDTO.getEmail());
        assertThat(driverPageDTO.getTotalPages()).isEqualTo(1);
        assertThat(driverPageDTO.getSize()).isEqualTo(10);
        assertThat(driverPageDTO.getNumber()).isEqualTo(0);
        assertThat(driverPageDTO.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindByIdWithNotExistDriver(){
        assertThrows(NotFoundException.class, () -> driverService.findById("100"));
    }

    @Test
    void testHardDeleteAndHardEvent(){
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        driverService.hardDelete(driverDTO.getId());

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("driver-hard-delete-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("driver-hard-delete-event-topic").get();
        assertThat(travelEvent.getDriverId()).isEqualTo(driverDTO.getId());
        assertThrows(NotFoundException.class, () -> driverService.findById(driverDTO.getId()));
    }

    @Test
    void testValidEvent() throws NotFoundException {
        DriverDTO driverDTO = new DriverDTO(
                "100",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);

        driverService.driverValidEvent(driverDTO.getId(), 33L);
        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("driver-valid-event-topic").isPresent());

        TravelEvent travelEvent = kafkaConsumer.getProcessedMessages("driver-valid-event-topic").get();
        assertThat(travelEvent.getDriverId()).isEqualTo(driverDTO.getId());
        assertThat(travelEvent.getRideId()).isEqualTo(33L);
        driverDTO.setStatus("BUSY");
        assertThat(driverService.findById(driverDTO.getId())).isEqualTo(driverDTO);
    }

    @AfterAll
    static void tearDown(){
        kafka.stop();
        mongoDBContainer.stop();
    }
}
