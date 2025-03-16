package org.example.integration;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Status;
import org.example.exceptions.NoWaitingRideException;
import org.example.integration.util.KafkaConsumer;
import org.example.repositories.RideRepository;
import org.example.services.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class RideServiceIntegrationTest {
    @Autowired
    private RideService rideService;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private KafkaConsumer kafkaConsumer;
    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
    private RideDTO testRide;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();

        testRide = new RideDTO(
                1L,
                2L,
                "3",
                "Point A",
                "Point B",
                Status.WAITING,
                false
        );
    }
    @Test
    void testCreateRide() {
        RideDTO savedRide = rideService.create(testRide);
        RideDTO res = rideService.findById(savedRide.getId());
        assertThat(savedRide).isNotNull().isEqualTo(res);
    }
    @Test
    void testUpdateRide() {
        RideDTO savedRide = rideService.create(testRide);

        RideDTO updatedRide = new RideDTO(
                1L,
                2L,
                "3",
                "New Point A",
                "New Point B",
                Status.WAITING,
                false
        );

        rideService.update(savedRide.getId(), updatedRide);
        RideDTO res = rideService.findById(savedRide.getId());

        assertThat(res).isNotNull();
        assertThat(res.getPointA()).isEqualTo(updatedRide.getPointA());
        assertThat(res.getPointB()).isEqualTo(updatedRide.getPointB());
    }
    @Test
    void testSoftDelete() {
        RideDTO savedRide = rideService.create(testRide);
        rideService.softDelete(savedRide.getId());

        assertTrue(rideService.findById(savedRide.getId()).isDeleted());
    }

    @Test
    void testHardDelete() {
        RideDTO savedRide = rideService.create(testRide);
        rideService.hardDelete(savedRide.getId());

        assertThatThrownBy(() -> rideService.findById(savedRide.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void testFindAllNotDeleted() {
        rideService.create(testRide);

        Pageable pageable = PageRequest.of(0, 2);
        RidePageDTO page = rideService.findAllNotDeleted(pageable);

        assertThat(page.getTotalElem()).isEqualTo(1);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getRides()).hasSize(1);
    }

    @Test
    void testCheckFreeRide() throws NoWaitingRideException {
        RideDTO savedRide = rideService.create(testRide);

        rideService.checkFreeRide(savedRide.getDriverId());

        await().atMost(3, TimeUnit.SECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("check-driver-event-topic").isPresent());

        TravelEvent event = kafkaConsumer.getProcessedMessages("check-driver-event-topic").get();
        assertThat(event.getRideId()).isEqualTo(savedRide.getId());
        assertThat(event.getDriverId()).isEqualTo(savedRide.getDriverId());
    }

    @Test
    void testCheckFreeRideWithNoWaitingRides() {
        assertThrows(NoWaitingRideException.class, () -> rideService.checkFreeRide("driver1"));
    }

    @Test
    void testStopTravel() throws EntityNotFoundException {
        RideDTO savedRide = rideService.create(testRide);
        rideService.attachDriver("3", savedRide.getId());

        rideService.stopTravel("3", 5.0, 100.0);

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> kafkaConsumer.getProcessedMessages("stop-travel-event-topic").isPresent());

        TravelEvent event = kafkaConsumer.getProcessedMessages("stop-travel-event-topic").get();
        assertThat(event.getRideId()).isEqualTo(savedRide.getId());
        assertThat(event.getDriverId()).isEqualTo("3");
        assertThat(event.getCostOfRide()).isEqualTo(100.0);
        assertThat(event.getRatingForPassenger()).isEqualTo(5.0);
    }

    @Test
    void testStopTravelWithNoActiveRide() {
        assertThrows(EntityNotFoundException.class, () -> rideService.stopTravel("driver1", 5.0, 100.0));
    }

    @Test
    void testAttachDriver() throws EntityNotFoundException {
        RideDTO savedRide = rideService.create(testRide);
        rideService.attachDriver("driver1", savedRide.getId());

        RideDTO ride = rideService.findById(savedRide.getId());
        assertThat(ride.getDriverId()).isEqualTo("driver1");
    }

    @Test
    void testAttachDriverToNonExistingRide() {
        assertThrows(EntityNotFoundException.class, () -> rideService.attachDriver("driver1", 999L));
    }
}
