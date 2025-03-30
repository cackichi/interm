package org.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic createDriverTopic(){
        return TopicBuilder.name("driver-create-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    public NewTopic hardDeleteDriverTopic(){
        return TopicBuilder.name("driver-hard-delete-event-topic")
                .partitions(3)
                .build();
    }
    @Bean
    public NewTopic softDeleteDriverTopic(){
        return TopicBuilder.name("driver-soft-delete-event-topic")
                .partitions(2)
                .build();
    }
    @Bean
    public NewTopic validDriverTopic(){
        return TopicBuilder.name("driver-valid-event-topic")
                .partitions(2)
                .build();
    }

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
