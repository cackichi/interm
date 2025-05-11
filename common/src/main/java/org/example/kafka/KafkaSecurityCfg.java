package org.example.kafka;

import org.example.asepcts.KafkaAspect;
import org.example.security.KeycloakService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaSecurityCfg {
    @Bean
    public KafkaAspect kafkaAspect(KeycloakService keycloakService){
        return new KafkaAspect(keycloakService);
    }
}
