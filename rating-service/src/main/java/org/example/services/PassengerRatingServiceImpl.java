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
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private ModelMapper modelMapper;

    @Override
    public PassengerRating mapToRating(PassengerRatingDTO dto){
        return modelMapper.map(dto, PassengerRating.class);
    }
    @Override
    public PassengerRatingDTO mapToDTO(PassengerRating passengerRating){
        return modelMapper.map(passengerRating, PassengerRatingDTO.class);
    }
    @Override
    public double findRating(Long id){
        return passengerRatingRepository.findRating(id).map(PassengerRating::getAverageRating).orElseThrow(() ->new EntityNotFoundException("Запись не найдена"));
    }
    @Override
    @Transactional
    public void updateOrSaveRating(Long id, double rating){
        passengerRatingRepository.updateOrSaveRating(id, rating);
    }
    @Override
    public List<PassengerRating> findAllNotDeleted(){
        return passengerRatingRepository.findAllNotDeleted();
    }
    @Override
    public void create(PassengerRatingDTO dto){
        passengerRatingRepository.save(mapToRating(dto));
    }
    @Override
    @Transactional
    public void softDelete(Long id){
        passengerRatingRepository.softDelete(id);
    }
    @Override
    @Transactional
    public void hardDelete(Long id){
        passengerRatingRepository.deleteById(id);
    }
}
