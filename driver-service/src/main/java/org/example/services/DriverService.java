package org.example.services;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.dto.TravelEvent;
import org.example.exceptions.BusyDriverException;
import org.example.exceptions.NotFoundException;
import org.springframework.data.domain.Pageable;

public interface DriverService {
    DriverDTO findById(String id) throws NotFoundException;

    void create(DriverDTO driverDTO);

    void update(String id, DriverDTO driverDTO) throws NotFoundException;

    void updateStatus(String id, String status) throws NotFoundException;

    void updateStatusForTravel(String id, String status) throws NotFoundException, BusyDriverException;

    void softDelete(String id);

    void hardDelete(String id);

    DriverPageDTO findAllNotDeleted(Pageable pageable);

    void driverValidEvent(String id, Long rideId);

    Driver mapToDriver(DriverDTO driverDTO);

    DriverDTO mapToDTO(Driver driver);

    void driverCreateEvent(String id);

    void driverHardDeleteEvent(String id);

    void driverSoftDeleteEvent(String id);

    ProducerRecord<String, TravelEvent> generateProducerRecord(String topic, TravelEvent travelEvent, String id);
}