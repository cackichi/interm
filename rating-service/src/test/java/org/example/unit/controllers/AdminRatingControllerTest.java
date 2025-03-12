package org.example.unit.controllers;

import org.example.controllers.AdminRatingController;
import org.example.services.DriverRatingService;
import org.example.services.PassengerRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminRatingControllerTest {
    @Mock
    private PassengerRatingService passengerRatingService;
    @Mock
    private DriverRatingService driverRatingService;
    @InjectMocks
    private AdminRatingController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void hardDeleteDriverRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/drivers/rating/{id}","1"))
                .andExpect(status().isNoContent());
        verify(driverRatingService).hardDelete("1");
    }

    @Test
    void hardDeletePassengerRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/passenger/rating/{id}",1))
                .andExpect(status().isNoContent());
        verify(passengerRatingService).hardDelete(1L);
    }
}