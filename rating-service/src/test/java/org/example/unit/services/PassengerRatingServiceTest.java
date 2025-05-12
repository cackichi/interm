package org.example.unit.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.PassengerRatingDTO;
import org.example.entities.PassengerRating;
import org.example.repositories.PassengerRatingRepository;
import org.example.services.PassengerRatingServiceImpl;
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
class PassengerRatingServiceTest {
    @Mock
    private PassengerRatingRepository passengerRatingRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PassengerRatingServiceImpl passengerRatingService;
    private PassengerRating passengerRating;
    private PassengerRatingDTO passengerRatingDTO;

    @BeforeEach
    void setUp() {
        passengerRating = new PassengerRating(
                1L,
                4.5,
                4,
                false
        );
        passengerRatingDTO = new PassengerRatingDTO(
                1L,
                4.5,
                4,
                false
        );
    }

    @Test
    void mapToRating() {
        when(modelMapper.map(any(PassengerRatingDTO.class), eq(PassengerRating.class)))
                .thenReturn(passengerRating);

        PassengerRating result = passengerRatingService.mapToRating(passengerRatingDTO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(passengerRating);
        verify(modelMapper).map(passengerRatingDTO, PassengerRating.class);
    }

    @Test
    void mapToDTO() {
        when(modelMapper.map(any(PassengerRating.class), eq(PassengerRatingDTO.class)))
                .thenReturn(passengerRatingDTO);

        PassengerRatingDTO result = passengerRatingService.mapToDTO(passengerRating);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(passengerRatingDTO);
        verify(modelMapper).map(passengerRating, PassengerRatingDTO.class);
    }

    @Test
    void findRating() {
        when(passengerRatingRepository.findRating(anyLong()))
                .thenReturn(Optional.of(passengerRating));

        double rating = passengerRatingService.findRating(1L);

        assertThat(rating).isEqualTo(4.5);
        verify(passengerRatingRepository).findRating(1L);
    }

    @Test
    void findRating_EntityNotFound() {
        when(passengerRatingRepository.findRating(anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> passengerRatingService.findRating(2L));
        assertThat(exception.getMessage()).isEqualTo("Запись не найдена");
        verify(passengerRatingRepository).findRating(2L);
    }

    @Test
    void updateOrSaveRating() {
        passengerRatingService.updateOrSaveRating(1L, 4.5);

        verify(passengerRatingRepository).updateOrSaveRating(1L, 4.5);
    }

    @Test
    void findAllNotDeleted() {
        List<PassengerRating> ratings = List.of(passengerRating);
        when(passengerRatingRepository.findAllNotDeleted())
                .thenReturn(ratings);

        List<PassengerRating> result = passengerRatingService.findAllNotDeleted();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(passengerRating);
        verify(passengerRatingRepository).findAllNotDeleted();
    }

    @Test
    void create() {
        when(modelMapper.map(any(PassengerRatingDTO.class), eq(PassengerRating.class)))
                .thenReturn(passengerRating);

        passengerRatingService.create(passengerRatingDTO);

        verify(modelMapper).map(passengerRatingDTO, PassengerRating.class);
        verify(passengerRatingRepository).save(passengerRating);
    }

    @Test
    void softDelete() {
        passengerRatingService.softDelete(1L);

        verify(passengerRatingRepository).softDelete(1L);
    }

    @Test
    void hardDelete() {
        passengerRatingService.hardDelete(1L);

        verify(passengerRatingRepository).deleteById(1L);
    }
}