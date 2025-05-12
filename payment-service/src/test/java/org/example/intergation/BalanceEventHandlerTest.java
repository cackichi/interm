package org.example.intergation;

import org.example.dto.BalanceDTO;
import org.example.dto.TravelEvent;
import org.example.repositories.BalanceRepository;
import org.example.services.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class BalanceEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @BeforeEach
    void setUp(){
        balanceRepository.deleteAll();
    }

    @Test
    void testCreateEvent(){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(2L);
        assertThat(balanceRepository.findAll()).hasSize(0);
        kafkaTemplate.send("passenger-create-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceRepository.findAll()).hasSize(1);
                });
    }

    @Test
    void testHardDeleteEvent(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(2L);
        balanceDTO.setBalance(1000);
        balanceDTO.setDeleted(false);

        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(balanceDTO.getPassengerId());
        balanceService.create(balanceDTO);
        kafkaTemplate.send("passenger-hard-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(5, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceRepository.findAll()).hasSize(0);
                });
    }

    @Test
    void testSoftDeleteEvent(){
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(2L);
        balanceDTO.setBalance(1000);
        balanceDTO.setDeleted(false);

        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(balanceDTO.getPassengerId());
        balanceService.create(balanceDTO);
        kafkaTemplate.send("passenger-soft-delete-event-topic", String.valueOf(travelEvent.getPassengerId()), travelEvent);

        await().atMost(10, TimeUnit.SECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    assertThat(balanceService.getBalance(balanceDTO.getPassengerId()).isDeleted()).isTrue();
                });
    }
}
