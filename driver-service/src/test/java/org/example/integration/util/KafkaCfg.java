package org.example.integration.util;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaCfg {
    @Bean
    public NewTopic checkDriverEventTopic(){
        return TopicBuilder.name("check-driver-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    public NewTopic stopTravelEventTopic(){
        return TopicBuilder.name("stop-travel-event-topic")
                .partitions(2)
                .build();
    }
}
