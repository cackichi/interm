package org.example.unit.services;

import io.micrometer.tracing.Tracer;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.BusyDriverException;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.example.services.DriverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
    @Mock
    private DriverRepository driverRepository;
    @InjectMocks
    private DriverServiceImpl driverService;
    @Mock
    private Tracer tracer;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private KafkaTemplate<String, TravelEvent> kafkaTemplate;
    private DriverDTO driverDTO;
    private Driver driver;

    @BeforeEach
    void setUp(){
        driverDTO = new DriverDTO(
                "1",
                "Radrigo",
                3,
                "80445345698",
                "example@gmail.com",
                false,
                "FREE",
                new ArrayList<>()
        );
        driver = new Driver(
                "1",
                "Radrigo",
                3,
                "80445345698",
                "example@gmail.com",
                false,
                "FREE",
                new ArrayList<>()
        );
    }

    @Test
    void mapToDriver(){
        when(modelMapper.map(any(), eq(Driver.class)))
                .thenReturn(driver);

        Driver res = driverService.mapToDriver(driverDTO);
        assertThat(res).isNotNull();
        assertThat(res.getId())
                .isEqualTo(driver.getId());

        verify(modelMapper).map(any(), eq(Driver.class));
    }
    @Test
    void mapToDTO(){
        when(modelMapper.map(any(), eq(DriverDTO.class)))
                .thenReturn(driverDTO);

        DriverDTO res = driverService.mapToDTO(driver);
        assertThat(res).isNotNull();
        assertThat(res.getId())
                .isEqualTo(driver.getId());

        verify(modelMapper).map(any(), eq(DriverDTO.class));
    }
    @Test
    void create(){
        when(modelMapper.map(any(), eq(Driver.class)))
                .thenReturn(driver);
        when(driverRepository.save(driver))
                .thenReturn(driver);
        when(kafkaTemplate.send(anyString(),anyString(),any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());

        driverService.create(driverDTO);
        verify(driverRepository).save(driver);
        verify(kafkaTemplate).send(eq("driver-create-event-topic"),
                anyString(), any(TravelEvent.class));
    }
    @Test
    void softDelete(){
        when(kafkaTemplate.send(anyString(),anyString(),any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());

        driverService.softDelete(driver.getId());

        verify(driverRepository).softDelete(driver.getId());
        verify(kafkaTemplate).send(eq("driver-soft-delete-event-topic"),
                anyString(), any(TravelEvent.class));
    }
    @Test
    void update() throws NotFoundException {
        when(driverRepository.findById(driver.getId()))
                .thenReturn(Optional.of(driver));

        driverService.update(driver.getId(),driverDTO);

        verify(driverRepository).save(driver);
        verify(modelMapper).map(driverDTO, driver);
    }
    @Test
    void updateStatusForTravel() throws BusyDriverException, NotFoundException {
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        driverService.updateStatusForTravel(driver.getId(), "BUSY");

        verify(driverRepository).findById(driver.getId());
        verify(driverRepository).updateStatus(driver.getId(), "BUSY");

        driver.setStatus("BUSY");
        assertThrows(BusyDriverException.class, () -> driverService.updateStatusForTravel(driver.getId(), "BUSY"));
    }
    @Test
    void updateStatus() throws NotFoundException {
        when(driverRepository.updateStatus(driver.getId(), "FREE"))
                .thenReturn(1);
        driverService.updateStatus(driver.getId(),"FREE");
        verify(driverRepository).updateStatus(driver.getId(), "FREE");

        when(driverRepository.updateStatus(driver.getId(), "FREE"))
                .thenReturn(0);
        assertThrows(NotFoundException.class, () -> driverService.updateStatus(driver.getId(), "FREE"));
    }
    @Test
    void findAllNotDeleted(){
        Pageable pageable = PageRequest.of(0, 10);
        List<Driver> drivers = List.of(driver);
        Page<Driver> page = new PageImpl<>(drivers, pageable, 0);
        when(driverRepository.findAllNotDeleted(pageable)).thenReturn(page);

        DriverPageDTO result = driverService.findAllNotDeleted(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(pageable.getPageSize(), result.getSize());
        assertEquals(pageable.getPageNumber(), result.getNumber());
    }
    @Test
    void findById() throws NotFoundException {
        when(driverRepository.findById(driver.getId()))
                .thenReturn(Optional.of(driver));
        when(modelMapper.map(any(), eq(DriverDTO.class)))
                .thenReturn(driverDTO);

        DriverDTO res = driverService.findById(driver.getId());

        assertThat(res).isNotNull();
        assertThat(res.getEmail()).isEqualTo(driver.getEmail());

        when(driverRepository.findById(driver.getId()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> driverService.findById(driver.getId()));
    }
    @Test
    void hardDelete(){
        when(kafkaTemplate.send(anyString(),anyString(),any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());

        driverService.hardDelete(driver.getId());

        verify(driverRepository).deleteById(driver.getId());
        verify(kafkaTemplate).send(eq("driver-hard-delete-event-topic"),
                anyString(), argThat(t -> t.getDriverId().equals(driver.getId())));
    }
    @Test
    void driverValidEvent(){
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(kafkaTemplate.send(anyString(),anyString(),any(TravelEvent.class)))
                .thenReturn(new CompletableFuture<>());

        driverService.driverValidEvent(driver.getId(), 10L);

        verify(kafkaTemplate).send(eq("driver-valid-event-topic"),
                anyString(), argThat(t -> t.getDriverId().equals(driver.getId()) && t.getRideId().equals(10L)));
        verify(driverRepository).findById(driver.getId());
        verify(driverRepository).updateStatus(driver.getId(), "BUSY");
    }
}
