package org.example.cfg;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic stopTravelTopic(){
        return TopicBuilder.name("stop-travel-event-topic")
                .partitions(4)
                .build();
    }

    @Bean
    public NewTopic checkDriverTopic(){
        return TopicBuilder.name("check-driver-event-topic")
                .partitions(2)
                .build();
    }
}
