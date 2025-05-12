package org.example.unit.controllers;

import org.example.controllers.PassengerAdminController;
import org.example.services.PassengerServiceImpl;
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
public class PassengerAdminControllerTest {
    @Mock
    private PassengerServiceImpl passengerService;
    @InjectMocks
    private PassengerAdminController passengerController;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passengerController).build();
    }
    @Test
    void hardDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/passenger/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(passengerService).hardDelete(1L);
    }
}
