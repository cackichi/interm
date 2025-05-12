package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.BalanceController;
import org.example.dto.BalanceDTO;
import org.example.services.BalanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BalanceDTO balanceDTO;


    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private BalanceController balanceController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController)
                .build();
        objectMapper = new ObjectMapper();
        balanceDTO = new BalanceDTO();
        balanceDTO.setPassengerId(1L);
        balanceDTO.setBalance(1000);
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/v1/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceDTO)))
                .andExpect(status().isCreated());

        verify(balanceService).create(balanceDTO);
    }

    @Test
    void getBalance() throws Exception {
        when(balanceService.getBalance(balanceDTO.getPassengerId()))
                .thenReturn(balanceDTO);

        mockMvc.perform(get("/api/v1/balance/{passengerId}", balanceDTO.getPassengerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passengerId").value(balanceDTO.getPassengerId()))
                .andExpect(jsonPath("$.balance").value(balanceDTO.getBalance()));

        verify(balanceService).getBalance(balanceDTO.getPassengerId());
    }

    @Test
    void topUp() throws Exception {
        double deposit = 500.0;

        mockMvc.perform(patch("/api/v1/balance/top-up/{passengerId}", balanceDTO.getPassengerId())
                        .param("deposit", String.valueOf(deposit)))
                .andExpect(status().isNoContent());

        verify(balanceService).topUpBalance(balanceDTO.getPassengerId(), deposit);
    }

    @Test
    void softDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/balance/{passengerId}", balanceDTO.getPassengerId()))
                .andExpect(status().isNoContent());

        verify(balanceService).softDelete(balanceDTO.getPassengerId());
    }
}