package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.repositories.PassengerRepo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Log4j2
public class PassengerService {
    private final PassengerRepo passengerRepo;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, PassengerDTO> kafkaTemplate;

    public PassengerDTO mapToDTO(Passenger passenger){
        return modelMapper.map(passenger, PassengerDTO.class);
    }

    public Passenger mapToPass(PassengerDTO passengerDTO){
        return modelMapper.map(passengerDTO, Passenger.class);
    }

    public Passenger save(PassengerDTO passengerDTO){
        passengerDTO.setStatus(Status.NOT_ACTIVE);
        Passenger passenger = passengerRepo.save(mapToPass(passengerDTO)) ;
        createPassengerEvent(passenger.getId());
        return passenger;
    }

    @Transactional
    public void softDelete(Long id){
        passengerRepo.softDelete(id);
        softDeletePassengerEvent(id);
    }

    @Transactional
    public void updatePass(Long id, PassengerDTO passengerDTO){
        passengerRepo.editData(id, passengerDTO.getName(), passengerDTO.getEmail(), passengerDTO.getPhoneNumber());
    }

    @Transactional
    public void hardDelete(Long id){
        passengerRepo.deleteById(id);
        hardDeletePassengerEvent(id);
    }

    public PassengerPageDTO findAllNotDeleted(Pageable pageable){
        List<Passenger> passengers = passengerRepo.findAllNotDeleted();
        int totalPassengers = passengers.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), totalPassengers);

        List<PassengerDTO> passengerDTOs = passengers.subList(start, end).stream()
                .map(this::mapToDTO)
                .toList();

        return new PassengerPageDTO(
                passengerDTOs,
                totalPassengers,
                (int) Math.ceil((double) totalPassengers / pageable.getPageSize()),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    public PassengerDTO findOne(Long id){
        return passengerRepo.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Пассажир с таким идентификатором не найден"));
    }

    public void orderTaxi(Long id){
        CompletableFuture<SendResult<String, PassengerDTO>> future = kafkaTemplate.send("order-taxi-event-topic", String.valueOf(id), new PassengerDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Order taxi topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }

    @Transactional
    public boolean checkExistsAndStatus(Long id) {
        return passengerRepo.updateStatus(Status.WAITING, id, Status.NOT_ACTIVE) > 0;
    }

    public void createPassengerEvent(Long id){
        CompletableFuture<SendResult<String, PassengerDTO>> future = kafkaTemplate.send("passenger-create-event-topic", String.valueOf(id), new PassengerDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
              log.error("Field to send message: {}", exception.getMessage());
            } else {
               log.info("Create topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }

    public void hardDeletePassengerEvent(Long id){
        CompletableFuture<SendResult<String, PassengerDTO>> future = kafkaTemplate.send("passenger-hard-delete-event-topic", String.valueOf(id), new PassengerDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Hard delete topic topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }

    public void softDeletePassengerEvent(Long id){
        CompletableFuture<SendResult<String, PassengerDTO>> future = kafkaTemplate.send("passenger-soft-delete-event-topic", String.valueOf(id), new PassengerDTO(id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Soft delete topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
}
