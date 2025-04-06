package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.dto.TravelEvent;
import org.example.entities.Ride;
import org.example.entities.Status;
import org.example.exceptions.NoWaitingRideException;
import org.example.repositories.RideRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, TravelEvent> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Ride mapToRide(RideDTO rideDTO) {
        return modelMapper.map(rideDTO, Ride.class);
    }

    @Override
    public RideDTO mapToDTO(Ride ride) {
        return modelMapper.map(ride, RideDTO.class);
    }

    @Override
    public RideDTO create(RideDTO rideDTO) {
        rideDTO.setStatus(Status.WAITING);
        Ride savedRide = rideRepository.save(mapToRide(rideDTO));
        log.debug("Created new ride with id: {}", savedRide.getId());
        return mapToDTO(savedRide);
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting ride with id: {}", id);
        rideRepository.softDelete(id);
    }

    @Override
    @Transactional
    public void update(Long id, RideDTO rideDTO) {
        log.info("Updating ride with id: {}", id);
        int i = rideRepository.update(id, rideDTO.getPointA(), rideDTO.getPointB());
        if (i == 0) {
            log.error("Ride not found with id: {}", id);
            throw new EntityNotFoundException("Поездка с таким номером не найдена");
        }
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting ride with id: {}", id);
        rideRepository.deleteById(id);
    }

    @Override
    public RidePageDTO findAllNotDeleted(Pageable pageable, int total) {
        List<Ride> rides;
        if(total <= 5) {
            rides = (List<Ride>) redisTemplate.opsForValue().get("rides");
            if (rides == null){
                rides = rideRepository.findAllNotDeleted();

                int cacheSize = Math.min(rides.size(), 5);
                List<Ride> ridesToCache = cacheSize > 0 ? new ArrayList<>(rides.subList(0, cacheSize)) : Collections.emptyList();

                if (!ridesToCache.isEmpty()) {
                    log.debug("Set rides to cache");
                    redisTemplate.opsForValue().set(
                            "rides",
                            ridesToCache,
                            Duration.ofMinutes(1)
                    );
                }
            } else {
                log.debug("The rides were taken from cache");
            }
        } else {
            log.debug("Wanted total rides more than 5, therefore the data is taken from the database");
            rides = rideRepository.findAllNotDeleted();
        }
        log.debug("Fetching all not deleted rides, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        int totalRides = rides.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min(start + pageable.getPageSize(), totalRides);

        List<RideDTO> rideDTOs = rides.subList(start, end).stream()
                .map(this::mapToDTO)
                .toList();

        return new RidePageDTO(
                rideDTOs,
                totalRides,
                (int) Math.ceil((double) totalRides / pageable.getPageSize()),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    @Override
    public RideDTO findById(Long id) {
        log.debug("Looking for ride with id: {}", id);
        Optional<Ride> ride = rideRepository.findById(id);
        return ride.map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.error("Ride not found with id: {}", id);
                    return new EntityNotFoundException("Поездка не найдена по id: " + id);
                });
    }

    @Override
    public void checkFreeRide(String driverId) throws NoWaitingRideException {
        log.debug("Checking free ride for driver: {}", driverId);
        Long rideId = rideRepository.getOneWait();
        if(rideId == null) {
            log.warn("No waiting rides available");
            throw new NoWaitingRideException("Нет ни одной ожидающей поездки");
        }
        else {
            TravelEvent travelEvent = new TravelEvent();
            travelEvent.setRideId(rideId);
            travelEvent.setDriverId(driverId);
            log.info("Sending check-driver event for ride: {}, driver: {}", rideId, driverId);
            CompletableFuture<SendResult<String, TravelEvent>> future = kafkaTemplate.send("check-driver-event-topic", driverId, travelEvent);
            future.whenComplete((result, exception) -> {
                if(exception != null) log.error("Fatal error of check-driver-event-topic: {}", exception.getMessage());
                else log.info("Correct work check-driver-event-topic: {}", result);
            });
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long rideId, Status status) {
        log.info("Updating status for ride {} to {}", rideId, status);
        rideRepository.updateStatus(rideId, status);
    }

    @Override
    @Transactional
    public void stopTravel(String driverId, double passengerRating, double costOfRide) {
        log.info("Stopping travel for driver: {}", driverId);
        Optional<Ride> optionalRide = rideRepository.findAfterStopTravel(driverId);
        if(optionalRide.isEmpty()) {
            log.error("No active ride found for driver: {}", driverId);
            throw new EntityNotFoundException("У водителя нет действующей поездок");
        }
        else {
            Ride ride = optionalRide.get();
            TravelEvent travelEvent = TravelEvent.builder()
                    .rideId(ride.getId())
                    .costOfRide(costOfRide)
                    .driverId(driverId)
                    .passengerId(ride.getPassengerId())
                    .ratingForPassenger(passengerRating)
                    .build();
            log.info("Sending stop-travel event for ride: {}", ride.getId());
            CompletableFuture<SendResult<String, TravelEvent>> future =
                    kafkaTemplate.send("stop-travel-event-topic", String.valueOf(ride.getId()), travelEvent);
            future.whenComplete((result, exception) -> {
                if(exception != null) log.error("Fatal error of stop-travel-event-topic: {}", exception.getMessage());
                else {
                    updateStatus(ride.getId(), Status.COMPLETE);
                    log.info("Correct work stop-travel-event-topic: {}", result);
                }
            });
        }
    }

    @Override
    @Transactional
    public void attachDriver(String driverId, Long rideId) {
        log.info("Attaching driver {} to ride {}", driverId, rideId);
        int i = rideRepository.attachDriver(driverId, rideId);
        if (i == 0) {
            log.error("Failed to attach driver {} to ride {}", driverId, rideId);
            throw new EntityNotFoundException("Поездка не найдена");
        }
    }
}
