package org.example.intergation.testcfg;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaCfg {
    @Bean
    NewTopic stopTravelTopic(){
        return TopicBuilder.name("stop-travel-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic createEventTopic(){
        return TopicBuilder.name("passenger-create-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic hardDeleteEventTopic(){
        return TopicBuilder.name("passenger-hard-delete-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic softDeleteEventTopic(){
        return TopicBuilder.name("passenger-soft-delete-event-topic")
                .partitions(2)
                .build();
    }
}
