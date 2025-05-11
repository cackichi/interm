package org.example.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.asepcts.KafkaHandler;
import org.example.dto.BalanceDTO;
import org.example.dto.TravelEvent;
import org.example.exception.NonRetryableException;
import org.example.services.BalanceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class BalanceEventHandler {
    private final BalanceService balanceService;

    @KafkaListener(topics = "passenger-create-event-topic")
    @KafkaHandler
    public void createBalanceEvent(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in create balance event");
            throw new NonRetryableException("Non retryable passenger id = null");
        }
        BalanceDTO balanceDTO = BalanceDTO.builder()
                .passengerId(travelEvent.getPassengerId())
                .balance(1000)
                .timeLastDeposit(LocalDateTime.now())
                .build();

        log.info("Creating balance for passenger {}", travelEvent.getPassengerId());
        balanceService.create(balanceDTO);
    }

    @KafkaListener(topics = "passenger-hard-delete-event-topic")
    @KafkaHandler
    public void hardDeleteBalanceEvent(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in hard delete balance event");
            throw new NonRetryableException("Non retryable passenger id = null");
        }
        log.info("Hard deleting balance for passenger {}", travelEvent.getPassengerId());
        balanceService.hardDelete(travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "passenger-soft-delete-event-topic")
    @KafkaHandler
    public void softDeleteBalanceEvent(ConsumerRecord<String, TravelEvent> record) {
        TravelEvent travelEvent = record.value();
        if (travelEvent.getPassengerId() == null) {
            log.error("Passenger id is null in soft delete balance event");
            throw new NonRetryableException("Non retryable passenger id = null");
        }

        log.info("Soft deleting balance for passenger {}", travelEvent.getPassengerId());
        balanceService.softDelete(travelEvent.getPassengerId());
    }
}