package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.DriverRatingController;
import org.example.dto.DriverRatingDTO;
import org.example.services.DriverRatingService;
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
class DriverRatingControllerTest {
    @Mock
    private DriverRatingService driverRatingService;
    @InjectMocks
    private DriverRatingController controller;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private DriverRatingDTO driverRatingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        driverRatingDTO = new DriverRatingDTO();
        driverRatingDTO.setDriverId("1");
        driverRatingDTO.setAverageRating(4.5);
        driverRatingDTO.setRatingCount(4);
    }

    @Test
    void testFindRating() throws Exception {
        when(driverRatingService.findRating(driverRatingDTO.getDriverId())).thenReturn(driverRatingDTO.getAverageRating());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/drivers/rating/{id}",driverRatingDTO.getDriverId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
        verify(driverRatingService).findRating(driverRatingDTO.getDriverId());
    }

    @Test
    void testUpdateRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/drivers/rating/{id}",driverRatingDTO.getDriverId())
                        .param("rating", "4"))
                .andExpect(status().isNoContent());
        verify(driverRatingService).updateOrSaveRating(driverRatingDTO.getDriverId(), 4);
    }

    @Test
    void testSoftDelete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/drivers/rating/{id}",driverRatingDTO.getDriverId()))
                .andExpect(status().isNoContent());
        verify(driverRatingService).softDelete(driverRatingDTO.getDriverId());
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/drivers/rating")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(driverRatingDTO)))
                .andExpect(status().isCreated());
        verify(driverRatingService).create(driverRatingDTO);
    }
}