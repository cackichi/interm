package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.repositories.PassengerRepo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepo passengerRepo;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, TravelEvent> kafkaTemplate;

    @Override
    public PassengerDTO mapToDTO(Passenger passenger) {
        log.debug("Mapping Passenger entity to DTO: {}", passenger);
        return modelMapper.map(passenger, PassengerDTO.class);
    }

    @Override
    public Passenger mapToPass(PassengerDTO passengerDTO) {
        log.debug("Mapping PassengerDTO to entity: {}", passengerDTO);
        return modelMapper.map(passengerDTO, Passenger.class);
    }

    @Override
    public Passenger save(PassengerDTO passengerDTO) {
        log.info("Attempting to save new passenger: {}", passengerDTO.getEmail());
        passengerDTO.setStatus(Status.NOT_ACTIVE);
        Passenger passenger = passengerRepo.save(mapToPass(passengerDTO));
        log.info("Passenger saved successfully with ID: {}", passenger.getId());

        createPassengerEvent(passenger.getId());
        return passenger;
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Attempting soft delete for passenger ID: {}", id);
        passengerRepo.softDelete(id);
        log.info("Passenger ID {} soft deleted successfully", id);
        softDeletePassengerEvent(id);
    }

    @Override
    @Transactional
    public void updatePass(Long id, PassengerDTO passengerDTO) {
        log.info("Updating passenger ID {} with data: {}", id, passengerDTO);
        passengerRepo.editData(id, passengerDTO.getName(), passengerDTO.getEmail(), passengerDTO.getPhoneNumber());
        log.info("Passenger ID {} updated successfully", id);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.warn("Attempting hard delete for passenger ID: {}", id);
        passengerRepo.deleteById(id);
        log.warn("Passenger ID {} hard deleted permanently", id);
        hardDeletePassengerEvent(id);
    }

    @Override
    public PassengerPageDTO findAllNotDeleted(Pageable pageable) {
        log.debug("Fetching all not deleted passengers, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        List<Passenger> passengers = passengerRepo.findAllNotDeleted();
        int totalPassengers = passengers.size();

        log.debug("Found {} not deleted passengers", totalPassengers);

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

    @Override
    public PassengerDTO findOne(Long id) {
        log.debug("Looking for passenger with ID: {}", id);
        return passengerRepo.findById(id)
                .map(passenger -> {
                    log.debug("Found passenger: {}", passenger);
                    return mapToDTO(passenger);
                })
                .orElseThrow(() -> {
                    log.error("Passenger not found with ID: {}", id);
                    return new EntityNotFoundException("Пассажир с таким идентификатором не найден");
                });
    }
    @Override
    public void orderTaxi(Long id, String pointA, String pointB){
        log.info("Ordering taxi for passenger ID: {}, route: {} -> {}", id, pointA, pointB);
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(id);
        travelEvent.setPointA(pointA);
        travelEvent.setPointB(pointB);

        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate
                .send(generateProducerRecord("order-taxi-event-topic", travelEvent, id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Order taxi topic work successfully: {}", result.getRecordMetadata());
                travelEventUpdate(Status.WAITING, id);
            }
        });
    }
    @Override
    @Transactional
    public boolean checkExistsAndStatus(Long id) {
        return passengerRepo.updateStatus(Status.WAITING, id, Status.NOT_ACTIVE) > 0;
    }
    @Override
    public void createPassengerEvent(Long id){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(id);

        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate
                .send(generateProducerRecord("passenger-create-event-topic", travelEvent, id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Create topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void hardDeletePassengerEvent(Long id){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(id);

        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate
                .send(generateProducerRecord("passenger-hard-delete-event-topic", travelEvent, id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Hard delete topic topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    public void softDeletePassengerEvent(Long id){
        TravelEvent travelEvent = new TravelEvent();
        travelEvent.setPassengerId(id);

        CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate
                .send(generateProducerRecord("passenger-soft-delete-event-topic", travelEvent, id));

        future.whenComplete((result, exception) -> {
            if(exception != null){
                log.error("Field to send message: {}", exception.getMessage());
            } else {
                log.info("Soft delete topic work successfully: {}", result.getRecordMetadata());
            }
        });
    }
    @Override
    @Transactional
    public void travelEventUpdate(Status newStatus, Long id){
        passengerRepo.updateBecauseOfTravel(newStatus, id);
    }

    @Override
    public ProducerRecord<String, TravelEvent> generateProducerRecord(String topic, TravelEvent travelEvent, Long id){
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = jwt.getTokenValue();

        ProducerRecord<String, TravelEvent> record = new ProducerRecord<>(
                topic,
                String.valueOf(id),
                travelEvent
        );

        record.headers().add(new RecordHeader("X-Access-Token", accessToken.getBytes()));

        return record;
    }
}