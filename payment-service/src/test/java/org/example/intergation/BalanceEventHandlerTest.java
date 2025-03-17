package org.example.intergation;

import org.example.dto.BalanceDTO;
import org.example.dto.TravelEvent;
import org.example.intergation.testcfg.KafkaCfg;
import org.example.intergation.testcfg.KafkaProducerConfig;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceService;
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

@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaCfg.class})
@Testcontainers
public class BalanceEventHandlerTest {
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Container
    static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));
    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);

        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    void setUp(){
        balanceRepository.deleteAll();
    }

    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(1L);
        assertThat(balanceRepository.findAll()).hasSize(0);
        kafkaTemplate.send("passenger-create-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceRepository.findAll()).hasSize(1);
                });
    }

    @Test
    void testHardDeleteEvent(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setDeleted(false);

        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(balanceDTO.getPassengerId());
        balanceService.create(balanceDTO);
        kafkaTemplate.send("passenger-hard-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceRepository.findAll()).hasSize(0);
                });
    }

    @Test
    void testSoftDeleteEvent(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
        balanceDTO.setDeleted(false);

        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(balanceDTO.getPassengerId());
        balanceService.create(balanceDTO);
        kafkaTemplate.send("passenger-soft-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceService.getBalance(balanceDTO.getPassengerId()).isDeleted()).isTrue();
                });
    }
}
