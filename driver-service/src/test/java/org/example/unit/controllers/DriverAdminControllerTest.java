package org.example.unit.controllers;

import org.example.controllers.DriverAdminController;
import org.example.services.DriverServiceImpl;
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
public class DriverAdminControllerTest {
    @InjectMocks
    private DriverAdminController driverController;
    @Mock
    private DriverServiceImpl driverService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(driverController).build();
    }

    @Test
    void hardDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/drivers/{id}", "1"))
                .andExpect(status().isNoContent());
        verify(driverService).hardDelete("1");
    }
}
