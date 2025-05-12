package org.example.unit.controllers;

import org.example.controllers.RideAdminController;
import org.example.services.RideServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RideAdminControllerTest {
    @Mock
    private RideServiceImpl rideService;
    @InjectMocks
    private RideAdminController rideController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
    }

    @Test
    void hardDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/rides/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(rideService).hardDelete(1L);
    }
}
