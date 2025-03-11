package org.example.services;

import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.repositories.PassengerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {
    @Mock
    private PassengerRepo passengerRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @InjectMocks
    private PassengerServiceImpl passengerService;
    private Passenger passenger;
    private PassengerDTO passengerDTO;

    @BeforeEach
    void setUp() {
        passenger = new Passenger(
                1L,
                "John Doe",
                "john@email.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );

        passengerDTO = new PassengerDTO(
                1L,
                "John Doe",
                "john@email.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
    }

    @Test
    void mapToDTO() {
        when(modelMapper.map(passenger, PassengerDTO.class))
                .thenReturn(passengerDTO);

        PassengerDTO result = passengerService.mapToDTO(passenger);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@email.com");
        verify(modelMapper).map(passenger, PassengerDTO.class);
    }

    @Test
    void mapToPass() {
        when(modelMapper.map(passengerDTO, Passenger.class))
                .thenReturn(passenger);

        Passenger result = passengerService.mapToPass(passengerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@email.com");
        verify(modelMapper).map(passengerDTO, Passenger.class);
    }

    @Test
    void save() {
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());
        when(passengerRepo.save(any(Passenger.class)))
                .thenReturn(passenger);
        when(modelMapper.map(passengerDTO, Passenger.class))
                .thenReturn(passenger);

        Passenger result = passengerService.save(passengerDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.NOT_ACTIVE);
        verify(passengerRepo).save(any(Passenger.class));
        verify(kafkaTemplate).send(anyString(), anyString(), any(TravelEvent.class));
    }

    @Test
    void softDelete() {
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());
        passengerService.softDelete(1L);

        verify(passengerRepo).softDelete(1L);
        verify(kafkaTemplate).send(anyString(), anyString(), any(TravelEvent.class));
    }

    @Test
    void updatePass() {
        passengerService.updatePass(1L, passengerDTO);

        verify(passengerRepo).editData(1L, passengerDTO.getName(), passengerDTO.getEmail(), passengerDTO.getPhoneNumber());
    }

    @Test
    void hardDelete() {
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());
        passengerService.hardDelete(1L);

        verify(passengerRepo).deleteById(1L);
        verify(kafkaTemplate).send(anyString(), anyString(), any(TravelEvent.class));
    }

    @Test
    void findAllNotDeleted() {
        List<Passenger> passengers = List.of(passenger);
        when(passengerRepo.findAllNotDeleted())
                .thenReturn(passengers);

        Pageable pageable = PageRequest.of(0, 10);

        PassengerPageDTO result = passengerService.findAllNotDeleted(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getPassengers()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(passengerRepo).findAllNotDeleted();
    }

    @Test
    void findOne() {
        when(passengerRepo.findById(1L))
                .thenReturn(Optional.of(passenger));
        when(modelMapper.map(passenger, PassengerDTO.class))
                .thenReturn(passengerDTO);

        PassengerDTO result = passengerService.findOne(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(passengerRepo).findById(1L);
    }

    @Test
    void orderTaxi() {
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());
        passengerService.orderTaxi(1L, "Москва", "Санкт-Петербург");

        verify(kafkaTemplate).send(
                eq("order-taxi-event-topic"),
                eq("1"),
                any(TravelEvent.class)
        );
    }

    @Test
    void checkExistsAndStatus() {
        when(passengerRepo.updateStatus(Status.WAITING, 1L, Status.NOT_ACTIVE))
                .thenReturn(1);

        boolean result = passengerService.checkExistsAndStatus(1L);

        assertThat(result).isTrue();
        verify(passengerRepo).updateStatus(Status.WAITING, 1L, Status.NOT_ACTIVE);
    }

    @Test
    void travelEventUpdate() {
        passengerService.travelEventUpdate(Status.WAITING, 1L);

        verify(passengerRepo).updateBecauseOfTravel(Status.WAITING, 1L);
    }
}
