package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.BusyDriverException;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @Override
    public Driver mapToDriver(DriverDTO driverDTO) {
        return modelMapper.map(driverDTO, Driver.class);
    }

    @Override
    public DriverDTO mapToDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }

    @Override
    public void create(DriverDTO driverDTO) {
        log.info("Creating new driver with data: {}", driverDTO);
        try {
            Driver driver = mapToDriver(driverDTO);
            driver.setStatus("FREE");
            Driver savedDriver = driverRepository.save(driver);
            log.debug("Driver created successfully with ID: {}", savedDriver.getId());
            driverCreateEvent(savedDriver.getId());
        } catch (Exception e) {
            log.error("Failed to create driver: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        log.info("Soft deleting driver with ID: {}", id);
        driverRepository.softDelete(id);
        driverSoftDeleteEvent(id);
        log.debug("Driver {} marked as deleted", id);
    }

    @Override
    @Transactional
    public void update(String id, DriverDTO driverDTO) throws NotFoundException {
        log.info("Updating driver with ID: {}, new data: {}", id, driverDTO);
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow(() -> {
            log.warn("Driver not found with ID: {}", id);
            return new NotFoundException("Такой водитель не найден");
        });

        modelMapper.map(driverDTO, existingDriver);
        driverRepository.save(existingDriver);
        log.debug("Driver {} updated successfully", id);
    }

    @Override
    public void updateStatusForTravel(String id, String status) throws NotFoundException, BusyDriverException {
        log.info("Updating status for driver {} to {}", id, status);
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow(() -> {
            log.warn("Driver not found with ID: {}", id);
            return new NotFoundException("Такой водитель не найден");
        });

        if(!existingDriver.getStatus().equals("FREE")) {
            log.warn("Driver {} is busy, current status: {}", id, existingDriver.getStatus());
            throw new BusyDriverException("Водитель в поездке");
        }

        existingDriver.setStatus(status);
        driverRepository.updateStatus(id, status);
        log.debug("Driver {} status updated to {}", id, status);
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status) throws NotFoundException {
        log.info("Updating status for driver {} to {}", id, status);
        int i = driverRepository.updateStatus(id, status);
        if(i == 0) {
            log.warn("Driver not found with ID: {}", id);
            throw new NotFoundException("Такой водитель не найден");
        }
        log.debug("Driver {} status updated successfully", id);
    }

    @Override
    public DriverPageDTO findAllNotDeleted(Pageable pageable) {
        log.debug("Fetching not deleted drivers, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Driver> driversPage = driverRepository.findAllNotDeleted(pageable);

        List<DriverDTO> driverDTOs = driversPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.debug("Found {} drivers on page {}", driverDTOs.size(), pageable.getPageNumber());
        return new DriverPageDTO(
                driverDTOs,
                driversPage.getTotalElements(),
                driversPage.getTotalPages(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    @Override
    public DriverDTO findById(String id) throws NotFoundException {
        log.debug("Looking for driver with ID: {}", id);
        return driverRepository.findById(id)
                .map(driver -> {
                    log.debug("Found driver with ID: {}", id);
                    return mapToDTO(driver);
                })
                .orElseThrow(() -> {
                    log.warn("Driver not found with ID: {}", id);
                    return new NotFoundException("Такой водитель не найден");
                });
    }

    @Override
    @Transactional
    public void hardDelete(String id) {
        log.info("Hard deleting driver with ID: {}", id);
        driverRepository.deleteById(id);
        driverHardDeleteEvent(id);
        log.debug("Driver {} permanently deleted", id);
    }

    @Override
    public void driverCreateEvent(String id){
        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate.send("driver-create-event-topic", id,new TravelEvent(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            }  else {
            log.info("Message sent successfully. Headers:");
            result.getProducerRecord().headers().forEach(header ->
                    log.info("Header {}: {}", header.key(), new String(header.value())));
        }
        });
    }
    @Override
    public void driverHardDeleteEvent(String id){
        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate.send("driver-hard-delete-event-topic", id, new TravelEvent(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Hard delete topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void driverSoftDeleteEvent(String id){
        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate.send("driver-soft-delete-event-topic", id, new TravelEvent(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Soft delete topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void driverValidEvent(String driverId, Long rideId){
        try {
            updateStatusForTravel(driverId, "BUSY");
            TravelEvent travelEvent = new TravelEvent();
            travelEvent.setDriverId(driverId);
            travelEvent.setRideId(rideId);
            CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate.send("driver-valid-event-topic", driverId, travelEvent);

            future.whenComplete((result, exception) -> {
                if(exception != null){
                    log.error("Field to send message: {}", exception.getMessage());
                } else {
                    log.info("Valid topic work successfully: {}", result.getRecordMetadata());
                }
            });
        } catch (NotFoundException | BusyDriverException e) {
            log.error("Logic error: {}", e.getMessage());
        }
    }
}
