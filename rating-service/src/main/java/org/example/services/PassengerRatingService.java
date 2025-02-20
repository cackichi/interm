package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private ModelMapper modelMapper;

    public PassengerRating mapToRating(PassengerRatingDTO dto){
        return modelMapper.map(dto, PassengerRating.class);
    }

    public PassengerRatingDTO mapToDTO(PassengerRating passengerRating){
        return modelMapper.map(passengerRating, PassengerRatingDTO.class);
    }

    private double findRating(Long id){
        return passengerRatingRepository.findRating(id).map(PassengerRating::getAverageRating).orElseThrow(() ->new EntityNotFoundException("Запись не найдена"));
    }

    @Transactional
    private void updateRating(Long id, double rating){
        passengerRatingRepository.updateRating(id, rating);
    }

    private List<PassengerRating> findAllNotDeleted(){
        return passengerRatingRepository.findAllNotDeleted();
    }

    private void create(PassengerRatingDTO dto){
        passengerRatingRepository.save(mapToRating(dto));
    }

    @Transactional
    public void softDelete(Long id){
        passengerRatingRepository.softDelete(id);
    }

    @Transactional
    public void hardDelete(Long id){
        passengerRatingRepository.deleteById(id);
    }
}
