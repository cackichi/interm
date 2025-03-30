package org.example.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PassengerRatingServiceImpl implements PassengerRatingService {
    private final PassengerRatingRepository passengerRatingRepository;
    private final ModelMapper modelMapper;

    @Override
    public PassengerRating mapToRating(PassengerRatingDTO dto) {
        return modelMapper.map(dto, PassengerRating.class);
    }

    @Override
    public PassengerRatingDTO mapToDTO(PassengerRating passengerRating) {
        return modelMapper.map(passengerRating, PassengerRatingDTO.class);
    }

    @Override
    public double findRating(Long id) {
        log.debug("Finding rating for passenger {}", id);
        return passengerRatingRepository.findRating(id)
                .map(PassengerRating::getAverageRating)
                .orElseThrow(() -> {
                    log.error("Passenger rating not found for id {}", id);
                    return new EntityNotFoundException("Запись не найдена");
                });
    }

    @Override
    @Transactional
    public void updateOrSaveRating(Long id, double rating) {
        log.info("Updating rating for passenger {} to {}", id, rating);
        passengerRatingRepository.updateOrSaveRating(id, rating);
    }

    @Override
    public List<PassengerRating> findAllNotDeleted() {
        log.debug("Finding all not deleted passenger ratings");
        return passengerRatingRepository.findAllNotDeleted();
    }

    @Override
    public void create(PassengerRatingDTO dto) {
        log.info("Creating new passenger rating for passenger {}", dto.getPassengerId());
        passengerRatingRepository.save(mapToRating(dto));
    }

    @Override
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting rating for passenger {}", id);
        passengerRatingRepository.softDelete(id);
    }

    @Override
    @Transactional
    public void hardDelete(Long id) {
        log.info("Hard deleting rating for passenger {}", id);
        passengerRatingRepository.deleteById(id);
    }
}