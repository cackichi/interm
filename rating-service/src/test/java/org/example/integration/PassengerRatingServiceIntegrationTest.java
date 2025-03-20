package org.example.integration;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PassengerRatingServiceIntegrationTest extends BaseIntegrationTest{
    @Autowired
    private PassengerRatingService passengerRatingService;
    @Autowired
    private PassengerRatingRepository passengerRatingRepository;

    @BeforeEach
    void setUp() {
        passengerRatingRepository.deleteAll();
    }
    @Test
    void testCreate(){
        PassengerRatingDTO passengerRatingDTO = new PassengerRatingDTO(
                100L,
                4.5,
                4,
                false
        );
        passengerRatingService.create(passengerRatingDTO);
        PassengerRating res = passengerRatingRepository.findById(passengerRatingDTO.getPassengerId()).orElseThrow();
        assertThat(res).isEqualTo(passengerRatingService.mapToRating(passengerRatingDTO));
    }
    @Test
    void notFoundRating(){
        assertThrows(EntityNotFoundException.class, () -> passengerRatingService.findRating(10L));
    }
    @Test
    void testUpdateRating(){
        PassengerRatingDTO passengerRatingDTO = new PassengerRatingDTO(
                100L,
                4.5,
                4,
                false
        );
        passengerRatingService.create(passengerRatingDTO);
        passengerRatingService.updateOrSaveRating(passengerRatingDTO.getPassengerId(), 4);
        passengerRatingService.updateOrSaveRating(passengerRatingDTO.getPassengerId() + 1, 4);

        PassengerRating res1 = passengerRatingRepository.findById(passengerRatingDTO.getPassengerId()).orElseThrow();
        assertThat(res1.getAverageRating()).isEqualTo(4.4);
        assertThat(res1.getRatingCount()).isEqualTo(5);

        PassengerRating res2 = passengerRatingRepository.findById(passengerRatingDTO.getPassengerId() + 1).orElseThrow();
        assertThat(res2.getAverageRating()).isEqualTo(4);
        assertThat(res2.getRatingCount()).isEqualTo(1);
    }
    @Test
    void softDelete(){
        PassengerRatingDTO passengerRatingDTO = new PassengerRatingDTO(
                100L,
                4.5,
                4,
                false
        );
        passengerRatingService.create(passengerRatingDTO);
        passengerRatingService.softDelete(passengerRatingDTO.getPassengerId());
        PassengerRating res = passengerRatingRepository.findById(passengerRatingDTO.getPassengerId()).orElseThrow();
        assertThat(res.isDeleted()).isTrue();
    }
    @Test
    void hardDelete(){
        PassengerRatingDTO passengerRatingDTO = new PassengerRatingDTO(
                100L,
                4.5,
                4,
                false
        );
        passengerRatingService.create(passengerRatingDTO);
        passengerRatingService.hardDelete(passengerRatingDTO.getPassengerId());
        assertThat(passengerRatingRepository.findById(passengerRatingDTO.getPassengerId()).isEmpty()).isTrue();
    }

    @AfterAll
    static void tearDown(){
        kafkaContainer.stop();
        database.stop();
    }
}
