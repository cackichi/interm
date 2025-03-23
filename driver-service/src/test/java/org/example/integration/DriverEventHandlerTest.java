package org.example.integration;

import org.example.collections.Car;
import org.example.dto.DriverDTO;
import org.example.dto.TravelEvent;
import org.example.repositories.DriverRepository;
import org.example.services.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

public class DriverEventHandlerTest extends BaseIntegrationTest{
    @Autowired
    private DriverService driverService;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @BeforeEach
    void setUp(){
        driverRepository.deleteAll();
    }

    @Test
    void testHandleStopTravel(){
        DriverDTO driverDTO = new DriverDTO(
                "55",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId(driverDTO.getId());
        travelEvent.setRideId(33L);
        kafkaTemplate.send("stop-travel-event-topic", driverDTO.getId(), travelEvent);

        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
                    assertThat(updatedDriver.getStatus()).isEqualTo("FREE");
                });
    }
    @Test
    void testHandleStartTravel(){
        DriverDTO driverDTO = new DriverDTO(
                "55",
                "Peter",
                3,
                "80298904563",
                "driverLui@gmail.com",
                false,
                "FREE",
                List.of(new Car("7458", "AUDI", "white", false))
        );

        driverService.create(driverDTO);
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setDriverId(driverDTO.getId());
        travelEvent.setRideId(33L);
        kafkaTemplate.send("check-driver-event-topic", driverDTO.getId(), travelEvent);

        await().atMost(30, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .pollDelay(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    DriverDTO updatedDriver = driverService.findById(driverDTO.getId());
                    assertThat(updatedDriver.getStatus()).isEqualTo("BUSY");
                });
    }
}
