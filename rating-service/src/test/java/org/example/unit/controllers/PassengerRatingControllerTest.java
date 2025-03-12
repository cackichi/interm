package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.PassengerRatingController;
import org.example.dto.PassengerRatingDTO;
import org.example.services.PassengerRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PassengerRatingControllerTest {
    @Mock
    private PassengerRatingService passengerRatingService;
    @InjectMocks
    private PassengerRatingController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PassengerRatingDTO passengerRatingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        passengerRatingDTO = new PassengerRatingDTO();
        passengerRatingDTO.setPassengerId(1L);
        passengerRatingDTO.setAverageRating(4.5);
        passengerRatingDTO.setRatingCount(4);
    }

    @Test
    void testFindRating() throws Exception {
        when(passengerRatingService.findRating(passengerRatingDTO.getPassengerId())).thenReturn(passengerRatingDTO.getAverageRating());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/passenger/rating/{id}",passengerRatingDTO.getPassengerId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
        verify(passengerRatingService).findRating(passengerRatingDTO.getPassengerId());
    }

    @Test
    void testUpdateRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/passenger/rating/{id}",passengerRatingDTO.getPassengerId())
                        .param("rating", "4"))
                .andExpect(status().isNoContent());
        verify(passengerRatingService).updateOrSaveRating(passengerRatingDTO.getPassengerId(), 4);
    }

    @Test
    void testSoftDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/passenger/rating/{id}",passengerRatingDTO.getPassengerId()))
                .andExpect(status().isNoContent());
        verify(passengerRatingService).softDelete(passengerRatingDTO.getPassengerId());
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/passenger/rating")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(passengerRatingDTO)))
                .andExpect(status().isCreated());
        verify(passengerRatingService).create(passengerRatingDTO);
    }
}