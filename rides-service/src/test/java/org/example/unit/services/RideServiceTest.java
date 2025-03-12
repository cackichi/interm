package org.example.unit.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Ride;
import org.example.entities.Status;
import org.example.exceptions.NoWaitingRideException;
import org.example.repositories.RideRepository;
import org.example.services.RideServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private RideServiceImpl rideService;
    private Ride ride;
    private RideDTO rideDTO;
    @BeforeEach
    void setUp(){
        ride = new Ride(
                1L,
                1L,
                "1",
                "Moskow",
                "Petersburg",
                Status.WAITING,
                false
        );
        rideDTO = new RideDTO(
                1L,
                1L,
                "1",
                "Moskow",
                "Petersburg",
                Status.WAITING,
                false
        );
    }
    @Test
    void mapToDTO(){
        when(modelMapper.map(ride, RideDTO.class))
                .thenReturn(rideDTO);
        RideDTO res = rideService.mapToDTO(ride);
        assertThat(res)
                .isNotNull()
                .isEqualTo(rideDTO);
        verify(modelMapper).map(ride, RideDTO.class);
    }
    @Test
    void mapToRide(){
        when(modelMapper.map(rideDTO, Ride.class))
                .thenReturn(ride);
        Ride res = rideService.mapToRide(rideDTO);
        assertThat(res)
                .isNotNull()
                .isEqualTo(ride);
        verify(modelMapper).map(rideDTO, Ride.class);
    }
    @Test
    void create(){
        when(modelMapper.map(rideDTO, Ride.class))
                .thenReturn(ride);
        rideService.create(rideDTO);
        verify(rideRepository).save(ride);
    }
    @Test
    void softDelete(){
        rideService.softDelete(ride.getId());
        verify(rideRepository).softDelete(ride.getId());
    }
    @Test
    void hardDelete(){
        rideService.hardDelete(ride.getId());
        verify(rideRepository).deleteById(ride.getId());
    }
    @Test
    void update(){
        when(rideRepository.update(ride.getId(), ride.getPointA(), ride.getPointB()))
                .thenReturn(1);
        rideService.update(ride.getId(), rideDTO);

        when(rideRepository.update(ride.getId(), ride.getPointA(), ride.getPointB()))
                .thenReturn(0);
        assertThrows(EntityNotFoundException.class, () -> rideService.update(ride.getId(), rideDTO));
        verify(rideRepository, times(2)).update(ride.getId(), ride.getPointA(), ride.getPointB());
    }
    @Test
    void findAllNotDeleted(){
        List<Ride> rides = List.of(ride);
        when(rideRepository.findAllNotDeleted())
                .thenReturn(rides);
        RidePageDTO ridePageDTO = rideService.findAllNotDeleted(PageRequest.of(0, 10));
        assertThat(ridePageDTO.getNumber()).isEqualTo(0);
        assertThat(ridePageDTO.getTotalPages()).isEqualTo(1);
        assertThat(ridePageDTO.getSize()).isEqualTo(10);
        assertThat(ridePageDTO.getTotalElem()).isEqualTo(1);
        verify(rideRepository).findAllNotDeleted();
    }
    @Test
    void findById(){
        when(rideRepository.findById(ride.getId()))
                .thenReturn(Optional.of(ride));
        when(modelMapper.map(ride, RideDTO.class))
                .thenReturn(rideDTO);

        RideDTO res = rideService.findById(ride.getId());
        assertThat(res)
                .isNotNull()
                .isEqualTo(rideDTO);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> rideService.findById(1L));
        verify(rideRepository, times(2)).findById(1L);
    }
    @Test
    void updateStatus(){
        rideService.updateStatus(ride.getId(), Status.TRAVELING);
        verify(rideRepository).updateStatus(ride.getId(), Status.TRAVELING);
    }
    @Test
    void checkFreeRide() throws NoWaitingRideException {
        when(rideRepository.getOneWait()).thenReturn(ride.getId());
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());

        rideService.checkFreeRide(ride.getDriverId());

        when(rideRepository.getOneWait()).thenReturn(null);
        assertThrows(NoWaitingRideException.class, () -> rideService.checkFreeRide(ride.getDriverId()));
        verify(kafkaTemplate, times(1)).send(
                eq("check-driver-event-topic"),
                anyString(),
                argThat(t -> t.getRideId().equals(ride.getId()) && t.getDriverId().equals(ride.getDriverId())));
    }
    @Test
    void stopTravel(){
        when(rideRepository.findAfterStopTravel(ride.getDriverId()))
                .thenReturn(Optional.of(ride));
        when(kafkaTemplate.send(anyString(), anyString(), any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());
        rideService.stopTravel(ride.getDriverId(), 4,35);

        when(rideRepository.findAfterStopTravel(ride.getDriverId()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> rideService.stopTravel(ride.getDriverId(), 4,35));

        verify(kafkaTemplate, times(1)).send(
                eq("stop-travel-event-topic"),
                anyString(),
                any(TravelEvent.class));
    }
    @Test
    void attachDriver(){
        when(rideRepository.attachDriver(ride.getDriverId(), ride.getId()))
                .thenReturn(1);
        rideService.attachDriver(ride.getDriverId(), ride.getId());

        when(rideRepository.attachDriver(ride.getDriverId(), ride.getId()))
                .thenReturn(0);
        assertThrows(EntityNotFoundException.class, () -> rideService.attachDriver(ride.getDriverId(), ride.getId()));
    }
}
