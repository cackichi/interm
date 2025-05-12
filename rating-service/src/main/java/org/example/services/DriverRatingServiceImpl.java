package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;
import org.example.repositories.DriverRatingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DriverRatingServiceImpl implements DriverRatingService {
    private final DriverRatingRepository driverRatingRepository;
    private final ModelMapper modelMapper;

    @Override
    public DriverRating mapToRating(DriverRatingDTO dto) {
        return modelMapper.map(dto, DriverRating.class);
    }

    @Override
    public DriverRatingDTO mapToDTO(DriverRating driverRating) {
        return modelMapper.map(driverRating, DriverRatingDTO.class);
    }

    @Override
    public double findRating(String id) {
        log.debug("Finding rating for driver {}", id);
        return driverRatingRepository.findRating(id)
                .map(DriverRating::getAverageRating)
                .orElseThrow(() -> {
                    log.error("Driver rating not found for id {}", id);
                    return new EntityNotFoundException("Запись не найдена");
                });
    }

    @Override
    @Transactional
    public void updateOrSaveRating(String id, double rating) {
        log.info("Updating rating for driver {} to {}", id, rating);
        driverRatingRepository.updateRating(id, rating);
    }

    @Override
    public List<DriverRating> findAllNotDeleted() {
        log.debug("Finding all not deleted driver ratings");
        return driverRatingRepository.findAllNotDeleted();
    }

    @Override
    public void create(DriverRatingDTO dto) {
        log.info("Creating new driver rating for driver {}", dto.getDriverId());
        driverRatingRepository.save(mapToRating(dto));
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        log.info("Soft deleting rating for driver {}", id);
        driverRatingRepository.softDelete(id);
    }

    @Override
    @Transactional
    public void hardDelete(String id) {
        log.info("Hard deleting rating for driver {}", id);
        driverRatingRepository.deleteById(id);
    }
}