package org.example.services;

import lombok.AllArgsConstructor;
import org.example.dto.PassengerDTO;
import org.example.entities.Passenger;
import org.example.repositories.PassengerRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PassengerService {
    private final PassengerRepo passengerRepo;
    private final ModelMapper modelMapper;

    public void save(PassengerDTO passengerDTO){
        passengerRepo.save(modelMapper.map(passengerDTO, Passenger.class));
    }

    public void softDelete(Long id){
        passengerRepo.softDelete(id);
    }
}
