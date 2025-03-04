package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
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
@Log4j2
public class DriverServiceImpl implements DriverService{
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, DriverDTO> kafkaTemplate;

    @Override
    public Driver mapToDriver(DriverDTO driverDTO){
        return modelMapper.map(driverDTO, Driver.class);
    }
    @Override
    public DriverDTO mapToDTO(Driver driver){
        return modelMapper.map(driver, DriverDTO.class);
    }

    @Override
    public void create(DriverDTO driverDTO){
        Driver driver = mapToDriver(driverDTO);
        driver.setStatus("FREE");
        Driver savedDriver = driverRepository.save(driver);
        driverCreateEvent(savedDriver.getId());
    }

    @Override
    @Transactional
    public void softDelete(String id){
        driverRepository.softDelete(id);
        driverSoftDeleteEvent(id);
    }

    @Override
    @Transactional
    public void update(String id, DriverDTO driverDTO) throws NotFoundException{
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow(() -> new NotFoundException("Такой водитель не найден"));
        modelMapper.map(driverDTO, existingDriver);
        driverRepository.save(existingDriver);
    }

    @Override
    @Transactional
    public void updateStatusForTravel(String id, String status) throws NotFoundException, BusyDriverException {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow(() -> new NotFoundException("Такой водитель не найден"));
        if(!existingDriver.getStatus().equals("FREE")) throw new BusyDriverException("Водитель в поездке");
        else {
            existingDriver.setStatus(status);
            driverRepository.save(existingDriver);
        }
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status) throws NotFoundException {
        int i = driverRepository.updateStatus(id, status);
        if(i == 0) throw new NotFoundException("Такой водитель не найден");
    }

    @Override
    public DriverPageDTO findAllNotDeleted(Pageable pageable){
        Page<Driver> driversPage = driverRepository.findAllNotDeleted(pageable);

        List<DriverDTO> driverDTOs = driversPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new DriverPageDTO(
                driverDTOs,
                driversPage.getTotalElements(),
                driversPage.getTotalPages(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    @Override
    public DriverDTO findById(String id) throws NotFoundException{
        return driverRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new NotFoundException("Такой водитель не найден"));
    }

    @Override
    @Transactional
    public void hardDelete(String id){
        driverRepository.deleteById(id);
        driverHardDeleteEvent(id);
    }

    @Override
    public void driverCreateEvent(String id){
        CompletableFuture<SendResult<String, DriverDTO>> future = kafkaTemplate.send("driver-create-event-topic", id,new DriverDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Create topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void driverHardDeleteEvent(String id){
        CompletableFuture<SendResult<String, DriverDTO>> future = kafkaTemplate.send("driver-hard-delete-event-topic", id, new DriverDTO(id));

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
        CompletableFuture<SendResult<String, DriverDTO>> future = kafkaTemplate.send("driver-soft-delete-event-topic", id, new DriverDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Soft delete topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void driverValidEvent(String id){
        try {
            updateStatusForTravel(id, "BUSY");
            CompletableFuture<SendResult<String, DriverDTO>> future = kafkaTemplate.send("driver-valid-event-topic", id, new DriverDTO(id));

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
