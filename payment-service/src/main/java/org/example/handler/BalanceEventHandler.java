package org.example.handler;

import lombok.AllArgsConstructor;
import org.example.dto.BalanceDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.NonRetryableException;
import org.example.services.BalanceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class BalanceEventHandler {
    private final BalanceService balanceService;

    @KafkaListener(topics = "passenger-create-event-topic")
    public void createBalanceEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable passenger id = null");
        BalanceDTO balanceDTO = BalanceDTO.builder()
                .passengerId(travelEvent.getPassengerId())
                .balance(1000)
                .timeLastDeposit(LocalDateTime.now())
                .build();
        balanceService.create(balanceDTO);
    }
    @KafkaListener(topics = "passenger-hard-delete-event-topic")
    public void hardDeleteBalanceEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable passenger id = null");
        balanceService.hardDelete(travelEvent.getPassengerId());
    }

    @KafkaListener(topics = "passenger-soft-delete-event-topic")
    public void softDeleteBalanceEvent(TravelEvent travelEvent){
        if(travelEvent.getPassengerId() == null) throw new NonRetryableException("Non retryable passenger id = null");
        balanceService.softDelete(travelEvent.getPassengerId());
    }

}
