package org.example.intergation;

import org.example.dto.TravelEvent;
import org.example.entities.Payment;
import org.example.intergation.testcfg.KafkaCfg;
import org.example.intergation.testcfg.KafkaProducerConfig;
import org.example.repositories.PaymentRepository;
import org.example.services.PaymentService;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaCfg.class})
@Testcontainers
public class PaymentEventHandlerTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
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
        paymentRepository.deleteAll();
    }

    @Test
    void testCreatePaymentEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(100L);
        travelEvent.setCostOfRide(300);
        travelEvent.setRideId(100L);
        kafkaTemplate.send("stop-travel-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    List<Payment> payments = paymentRepository.findAll();
                    assertThat(payments).hasSize(1);
                    Payment payment = payments.get(0);
                    assertThat(payment.getCost()).isEqualTo(travelEvent.getCostOfRide());
                    assertThat(payment.getRideId()).isEqualTo(travelEvent.getRideId());
                });
    }
}
