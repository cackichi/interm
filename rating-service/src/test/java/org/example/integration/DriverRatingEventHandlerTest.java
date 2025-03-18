package org.example.integration;

import org.example.dto.TravelEvent;
import org.example.integration.testcfg.KafkaConfig;
import org.example.integration.testcfg.KafkaProducerConfig;
import org.example.repositories.DriverRatingRepository;
import org.example.services.DriverRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaConfig.class})
@Testcontainers
public class DriverRatingEventHandlerTest {
    @Autowired
    private DriverRatingService driverRatingService;
    @Autowired
    private DriverRatingRepository driverRatingRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);
    @Container
    static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
    @BeforeEach
    void setUp(){
        driverRatingRepository.deleteAll();
    }
    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("100");
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isPresent()).isFalse();
        kafkaTemplate.send("driver-create-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isPresent()).isTrue();
                });
    }
    @Test
    void testSoftDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("100");
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 5);
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).orElseThrow().isDeleted()).isFalse();
        kafkaTemplate.send("driver-soft-delete-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).orElseThrow().isDeleted()).isTrue();
                });
    }
    @Test
    void testHardDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId("100");
        driverRatingService.updateOrSaveRating(travelEvent.getDriverId(), 5);
        assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isEmpty()).isFalse();
        kafkaTemplate.send("driver-hard-delete-event-topic", String.valueOf(travelEvent.getDriverId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(driverRatingRepository.findById(travelEvent.getDriverId()).isEmpty()).isTrue();
                });
    }
}
