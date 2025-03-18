package org.example.integration.testcfg;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaConfig {
    @Bean
    NewTopic createPassengerEventTopic(){
        return TopicBuilder.name("passenger-create-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic softDeletePassengerEventTopic(){
        return TopicBuilder.name("passenger-soft-delete-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic hardDeletePassengerEventTopic(){
        return TopicBuilder.name("passenger-hard-delete-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic stopTravelEvent(){
        return TopicBuilder.name("stop-travel-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic createDriverEventTopic(){
        return TopicBuilder.name("driver-create-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic softDeleteDriverEventTopic(){
        return TopicBuilder.name("driver-soft-delete-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    NewTopic hardDeleteDriverEventTopic(){
        return TopicBuilder.name("driver-hard-delete-event-topic")
                .partitions(2)
                .build();
    }
}
