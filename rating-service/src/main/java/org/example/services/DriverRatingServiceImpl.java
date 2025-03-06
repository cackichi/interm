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
public class DriverRatingServiceImpl implements DriverRatingService {
    private final DriverRatingRepository driverRatingRepository;
    private final ModelMapper modelMapper;
    @Override
    public DriverRating mapToRating(DriverRatingDTO dto){
        return modelMapper.map(dto, DriverRating.class);
    }
    @Override
    public DriverRatingDTO mapToDTO(DriverRating driverRating){
        return modelMapper.map(driverRating, DriverRatingDTO.class);
    }
    @Override
    public double findRating(String id){
        return driverRatingRepository.findRating(id).map(DriverRating::getAverageRating).orElseThrow(() -> new EntityNotFoundException("Запись не найдена"));
    }
    @Override
    @Transactional
    public void updateOrSaveRating(String id, double rating){
        driverRatingRepository.updateRating(id, rating);
    }
    @Override
    public List<DriverRating> findAllNotDeleted(){
        return driverRatingRepository.findAllNotDeleted();
    }
    @Override
    public void create(DriverRatingDTO dto){
        driverRatingRepository.save(mapToRating(dto));
    }
    @Override
    @Transactional
    public void softDelete(String id){
        driverRatingRepository.softDelete(id);
    }
    @Override
    @Transactional
    public void hardDelete(String id){
        driverRatingRepository.deleteById(id);
    }
}
