package org.example.intergation;

import org.example.intergation.testcfg.KafkaCfg;
import org.example.intergation.testcfg.KafkaProducerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = {KafkaProducerConfig.class, KafkaCfg.class})
@Testcontainers
public class BaseIntegrationTest {
    static ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));
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

    static {
        kafkaContainer.start();
        database.start();
    }
}
