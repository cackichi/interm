package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;
import org.example.repositories.DriverRatingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverRatingService {
    private final DriverRatingRepository driverRatingRepository;
    private final ModelMapper modelMapper;

    public DriverRating mapToRating(DriverRatingDTO dto){
        return modelMapper.map(dto, DriverRating.class);
    }

    public DriverRatingDTO mapToDTO(DriverRating driverRating){
        return modelMapper.map(driverRating, DriverRatingDTO.class);
    }

    private double findRating(Long id){
        return driverRatingRepository.findRating(id).map(DriverRating::getAverageRating).orElseThrow(() ->new EntityNotFoundException("Запись не найдена"));
    }

    @Transactional
    private void updateRating(Long id, double rating){
        driverRatingRepository.updateRating(id, rating);
    }

    private List<DriverRating> findAllNotDeleted(){
        return driverRatingRepository.findAllNotDeleted();
    }

    private void create(DriverRatingDTO dto){
        driverRatingRepository.save(mapToRating(dto));
    }

    @Transactional
    public void softDelete(Long id){
        driverRatingRepository.softDelete(id);
    }

    @Transactional
    public void hardDelete(Long id){
        driverRatingRepository.deleteById(id);
    }
}
