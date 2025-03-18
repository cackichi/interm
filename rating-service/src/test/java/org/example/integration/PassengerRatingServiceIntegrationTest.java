package org.example.integration;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
)
@Testcontainers
public class PassengerRatingServiceIntegrationTest {
    @Autowired
    private PassengerRatingService passengerRatingService;
    @Autowired
    private PassengerRatingRepository passengerRatingRepository;
    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
    }

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
}
