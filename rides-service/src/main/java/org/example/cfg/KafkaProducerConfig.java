package org.example.cfg;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.dto.TravelEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaProducerConfig {
    private final Environment environment;

    Map<String, Object> prodConfig(){
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "20000");
        config.put(ProducerConfig.LINGER_MS_CONFIG, "0");
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "10000");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        return config;
    }

    @Bean
    ProducerFactory<String, TravelEvent> producerFactory(){
        return new DefaultKafkaProducerFactory<>(prodConfig());
    }

    @Bean
    KafkaTemplate<String, TravelEvent> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }
}
