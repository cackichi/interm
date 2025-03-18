package org.example.integration;

import org.example.dto.PassengerRatingDTO;
import org.example.dto.TravelEvent;
import org.example.integration.testcfg.KafkaConfig;
import org.example.integration.testcfg.KafkaProducerConfig;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingService;
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

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaConfig.class})
@Testcontainers
public class PassengerRatingEventHandlerTest {
    @Autowired
    private PassengerRatingService passengerRatingService;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Autowired
    private PassengerRatingRepository passengerRatingRepository;
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
        passengerRatingRepository.deleteAll();
    }
    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isPresent()).isFalse();
        kafkaTemplate.send("passenger-create-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isPresent()).isTrue();
                });
    }
    @Test
    void testSoftDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 5);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).orElseThrow().isDeleted()).isFalse();
        kafkaTemplate.send("passenger-soft-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).orElseThrow().isDeleted()).isTrue();
                });
    }
    @Test
    void testHardDeleteEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
        passengerRatingService.updateOrSaveRating(travelEvent.getPassengerId(), 5);
        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isEmpty()).isFalse();
        kafkaTemplate.send("passenger-hard-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).isEmpty()).isTrue();
                });
    }
    @Test
    void testStopTravelEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
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
                .untilAsserted(() -> {
                    assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).get().getAverageRating())
                            .isEqualTo(4.625);
                });

        travelEvent.setPassengerId(101L);
        travelEvent.setRatingForPassenger(3.45);
        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);
        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    if(!passengerRatingRepository.findAll().isEmpty()) {
                        assertThat(passengerRatingRepository.findById(travelEvent.getPassengerId()).get().getAverageRating())
                                .isEqualTo(travelEvent.getRatingForPassenger());
                    }
                });
    }
}
