package org.example.unit.controllers;


import org.example.controllers.AdminController;
import org.example.services.BalanceServiceImpl;
import org.example.services.PaymentServiceImpl;
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
public class AdminControllerTest {
    @Mock
    private PaymentServiceImpl paymentService;
    @Mock
    private BalanceServiceImpl balanceService;
    @InjectMocks
    private AdminController adminController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void hardDeleteBalance() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/balance/{passengerId}", 1L))
                .andExpect(status().isNoContent());

        verify(balanceService).hardDelete(1L);
    }

    @Test
    void hardDeletePayment() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/payment/{passengerId}", 1L))
                .andExpect(status().isNoContent());

        verify(paymentService).hardDelete(1L);
    }
}
