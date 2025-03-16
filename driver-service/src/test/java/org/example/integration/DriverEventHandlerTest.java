package org.example.integration;

import org.example.collections.Car;
import org.example.dto.DriverDTO;
import org.example.dto.TravelEvent;
import org.example.repositories.DriverRepository;
import org.example.services.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class DriverEventHandlerTest {
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));
    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp(){
        driverRepository.deleteAll();
    }

    @Test
    void testHandleStopTravel(){
        DriverDTO driverDTO = new DriverDTO(
                "55",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId(driverDTO.getId());
        travelEvent.setRideId(33L);
        kafkaTemplate.send("stop-travel-event-topic", driverDTO.getId(), travelEvent);

        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
                    assertThat(updatedDriver.getStatus()).isEqualTo("FREE");
                });
    }
    @Test
    void testHandleStartTravel(){
        DriverDTO driverDTO = new DriverDTO(
                "55",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId(driverDTO.getId());
        travelEvent.setRideId(33L);
        kafkaTemplate.send("check-driver-event-topic", driverDTO.getId(), travelEvent);

        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
                    assertThat(updatedDriver.getStatus()).isEqualTo("BUSY");
                });
    }
}
