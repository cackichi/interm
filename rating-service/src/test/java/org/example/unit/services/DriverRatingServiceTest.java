package org.example.unit.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.DriverRatingDTO;
import org.example.entities.DriverRating;
import org.example.repositories.DriverRatingRepository;
import org.example.services.DriverRatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DriverRatingServiceTest {
    @Mock
    private DriverRatingRepository driverRatingRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DriverRatingServiceImpl driverRatingService;
    private DriverRating driverRating;
    private DriverRatingDTO driverRatingDTO;

    @BeforeEach
    void setUp() {
        driverRating = new DriverRating(
                "1",
                4.5,
                4,
                false
        );
        driverRatingDTO = new DriverRatingDTO(
                "1",
                4.5,
                4,
                false
        );
    }

    @Test
    void mapToRating() {
        when(modelMapper.map(any(DriverRatingDTO.class), eq(DriverRating.class)))
                .thenReturn(driverRating);

        DriverRating result = driverRatingService.mapToRating(driverRatingDTO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(driverRating);
        verify(modelMapper).map(driverRatingDTO, DriverRating.class);
    }

    @Test
    void mapToDTO() {
        when(modelMapper.map(any(DriverRating.class), eq(DriverRatingDTO.class)))
                .thenReturn(driverRatingDTO);

        DriverRatingDTO result = driverRatingService.mapToDTO(driverRating);


        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(driverRatingDTO);
        verify(modelMapper).map(driverRating, DriverRatingDTO.class);
    }

    @Test
    void findRating() {
        when(driverRatingRepository.findRating(anyString()))
                .thenReturn(Optional.of(driverRating));


        double rating = driverRatingService.findRating("1");


        assertThat(rating).isEqualTo(4.5);
        verify(driverRatingRepository).findRating("1");
    }

    @Test
    void findRating_EntityNotFound() {
        when(driverRatingRepository.findRating(anyString()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> driverRatingService.findRating("2"));
        assertThat(exception.getMessage()).isEqualTo("Запись не найдена");
        verify(driverRatingRepository).findRating("2");
    }

    @Test
    void updateOrSaveRating() {
        driverRatingService.updateOrSaveRating("1", 4.5);

        verify(driverRatingRepository).updateRating("1", 4.5);
    }

    @Test
    void findAllNotDeleted() {
        List<DriverRating> ratings = List.of(driverRating);
        when(driverRatingRepository.findAllNotDeleted())
                .thenReturn(ratings);

        List<DriverRating> result = driverRatingService.findAllNotDeleted();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(driverRating);
        verify(driverRatingRepository).findAllNotDeleted();
    }

    @Test
    void create() {
        when(modelMapper.map(any(DriverRatingDTO.class), eq(DriverRating.class)))
                .thenReturn(driverRating);

        driverRatingService.create(driverRatingDTO);

        verify(modelMapper).map(driverRatingDTO, DriverRating.class);
        verify(driverRatingRepository).save(driverRating);
    }

    @Test
    void softDelete() {
        driverRatingService.softDelete("1");

        verify(driverRatingRepository).softDelete("1");
    }

    @Test
    void hardDelete() {
        driverRatingService.hardDelete("1");

        verify(driverRatingRepository).deleteById("1");
    }
}