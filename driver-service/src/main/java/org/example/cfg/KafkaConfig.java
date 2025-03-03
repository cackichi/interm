package org.example.cfg;

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
}
