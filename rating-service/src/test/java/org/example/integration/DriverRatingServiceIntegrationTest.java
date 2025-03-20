package org.example.integration;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;
import org.example.repositories.DriverRatingRepository;
import org.example.services.DriverRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DriverRatingServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private DriverRatingService driverRatingService;
    @Autowired
    private DriverRatingRepository driverRatingRepository;

    @BeforeEach
    void setUp() {
        driverRatingRepository.deleteAll();
    }
    @Test
    void testCreate(){
        DriverRatingDTO driverRatingDTO = new DriverRatingDTO(
                "100",
                4.5,
                4,
                false
        );
        driverRatingService.create(driverRatingDTO);
        DriverRating res = driverRatingRepository.findById(driverRatingDTO.getDriverId()).orElseThrow();
        assertThat(res).isEqualTo(driverRatingService.mapToRating(driverRatingDTO));
    }
    @Test
    void notFoundRating(){
        assertThrows(EntityNotFoundException.class, () -> driverRatingService.findRating("10"));
    }
    @Test
    void testUpdateRating(){
        DriverRatingDTO driverRatingDTO = new DriverRatingDTO(
                "100",
                4.5,
                4,
                false
        );
        driverRatingService.create(driverRatingDTO);
        driverRatingService.updateOrSaveRating(driverRatingDTO.getDriverId(), 4);
        driverRatingService.updateOrSaveRating(driverRatingDTO.getDriverId() + 1, 4);

        DriverRating res1 = driverRatingRepository.findById(driverRatingDTO.getDriverId()).orElseThrow();
        assertThat(res1.getAverageRating()).isEqualTo(4.4);
        assertThat(res1.getRatingCount()).isEqualTo(5);

        DriverRating res2 = driverRatingRepository.findById(driverRatingDTO.getDriverId() + 1).orElseThrow();
        assertThat(res2.getAverageRating()).isEqualTo(4);
        assertThat(res2.getRatingCount()).isEqualTo(1);
    }
    @Test
    void softDelete(){
        DriverRatingDTO driverRatingDTO = new DriverRatingDTO(
                "100",
                4.5,
                4,
                false
        );
        driverRatingService.create(driverRatingDTO);
        driverRatingService.softDelete(driverRatingDTO.getDriverId());
        DriverRating res = driverRatingRepository.findById(driverRatingDTO.getDriverId()).orElseThrow();
        assertThat(res.isDeleted()).isTrue();
    }
    @Test
    void hardDelete(){
        DriverRatingDTO driverRatingDTO = new DriverRatingDTO(
                "100",
                4.5,
                4,
                false
        );
        driverRatingService.create(driverRatingDTO);
        driverRatingService.hardDelete(driverRatingDTO.getDriverId());
        assertThat(driverRatingRepository.findById(driverRatingDTO.getDriverId()).isEmpty()).isTrue();
    }
}
