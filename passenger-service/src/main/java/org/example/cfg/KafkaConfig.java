package org.example.cfg;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic createPassengerTopic(){
        return TopicBuilder.name("passenger-create-event-topic")
                .partitions(2)
                .build();
    }

    @Bean
    public NewTopic hardDeletePassengerTopic(){
        return TopicBuilder.name("passenger-hard-delete-event-topic")
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic softDeletePassengerTopic(){
        return TopicBuilder.name("passenger-soft-delete-event-topic")
                .partitions(2)
                .build();
    }

    @Bean
    public NewTopic orderTaxiEvent(){
        return TopicBuilder.name("order-taxi-event-topic")
                .partitions(2)
                .build();
    }
}
