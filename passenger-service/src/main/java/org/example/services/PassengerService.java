package org.example.services;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.springframework.data.domain.Pageable;

public interface PassengerService {
    PassengerDTO mapToDTO(Passenger passenger);

    Passenger mapToPass(PassengerDTO passengerDTO);

    Passenger save(PassengerDTO passengerDTO);

    void softDelete(Long id);

    void updatePass(Long id, PassengerDTO passengerDTO);

    void hardDelete(Long id);

    PassengerPageDTO findAllNotDeleted(Pageable pageable);

    PassengerDTO findOne(Long id);

    void orderTaxi(Long id, String pointA, String pointB);

    boolean checkExistsAndStatus(Long id);

    void createPassengerEvent(Long id);

    void hardDeletePassengerEvent(Long id);

    void softDeletePassengerEvent(Long id);

    void travelEventUpdate(Status newStatus, Long id);

    ProducerRecord<String, TravelEvent> generateProducerRecord(String topic, TravelEvent travelEvent, Long id);
}
