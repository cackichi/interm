package org.example.integration;

import org.example.dto.PassengerDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.repositories.PassengerRepo;
import org.example.services.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class PassengerTravelEventHandlerTest {
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
                .untilAsserted(() -> {
                    PassengerDTO updatedPassenger = passengerService.findOne(savedPass.getId());
                    assertThat(updatedPassenger.getStatus()).isEqualTo(Status.TRAVELING);
                });
    }
}
