package org.example.services;

import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.entities.Ride;
import org.example.entities.Status;
import org.example.exceptions.NoWaitingRideException;
import org.springframework.data.domain.Pageable;

public interface RideService {
    Ride mapToRide(RideDTO rideDTO);
    RideDTO mapToDTO(Ride ride);
    RideDTO create(RideDTO rideDTO);
    void softDelete(Long id);
    void update(Long id, RideDTO rideDTO);
    void hardDelete(Long id);
    RidePageDTO findAllNotDeleted(Pageable pageable, int total);
    RideDTO findById(Long id);
    void checkFreeRide(String driverId) throws NoWaitingRideException;
    void updateStatus(Long rideId, Status status);
    void stopTravel(String driverId, double passengerRating, double costOfRide);
    void attachDriver(String driverId, Long rideId);
}
