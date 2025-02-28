package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.entities.Ride;
import org.example.repositories.RideRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    public Ride mapToRide(RideDTO rideDTO){
        return modelMapper.map(rideDTO, Ride.class);
    }

    public RideDTO mapToDTO(Ride ride){
        return modelMapper.map(ride, RideDTO.class);
    }

    public void create(RideDTO rideDTO){
        rideRepository.save(mapToRide(rideDTO));
    }

    @Transactional
    public void softDelete(Long id){
        rideRepository.softDelete(id);
    }

    @Transactional
    public void update(Long id, RideDTO rideDTO){
        rideRepository.update(id, rideDTO.getPointA(), rideDTO.getPointB());
    }

    @Transactional
    public void hardDelete(Long id){
        rideRepository.deleteById(id);
    }

    public RidePageDTO findAllNotDeleted(Pageable pageable){
        List<Ride> rides =  rideRepository.findAllNotDeleted();
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

    // Обновляем статус поездки после взаимодействия водителя
    public void updateStatus(Long id ,String status){
        rideRepository.updateStatus(id, status);
    }

    // Получаем id поездки со статусом WAITING и возвращаем true если она существует
    public boolean getOneWaiting(){
        Long id = rideRepository.getOneWait();
        return id != null;
    }

    public RideDTO findById(Long id){
        Optional<Ride> ride = rideRepository.findById(id);
        return ride.map(this::mapToDTO).orElseThrow(() -> new EntityNotFoundException("Поездка не найдена по id: " + id));
    }
}
