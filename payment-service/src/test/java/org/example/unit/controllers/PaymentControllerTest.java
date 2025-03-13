package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.PaymentController;
import org.example.dto.PaymentDTO;
import org.example.dto.PaymentPageDTO;
import org.example.entities.Status;
import org.example.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    private PaymentDTO paymentDTO;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .build();
        objectMapper = new ObjectMapper();
        paymentDTO = new PaymentDTO(
                1L,
                1L,
                1L,
                350.0,
                Status.WAITING,
                false
        );
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentDTO)))
                .andExpect(status().isCreated());

        verify(paymentService).create(paymentDTO);
    }

    @Test
    void closePayment() throws Exception {
        mockMvc.perform(patch("/api/v1/payment/close/{passengerId}", paymentDTO.getPassengerId()))
                .andExpect(status().isNoContent());

        verify(paymentService).closePayment(paymentDTO.getPassengerId());
    }

    @Test
    void getUnpaid() throws Exception {
        PaymentPageDTO expectedPage = new PaymentPageDTO(
                List.of(paymentDTO),
                1,
                1,
                10,
                0
        );
        when(paymentService.getUnpaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10)))
                .thenReturn(expectedPage);

        mockMvc.perform(get("/api/v1/payment/unpaid/{passengerId}", paymentDTO.getPassengerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].passengerId").value(paymentDTO.getPassengerId()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(paymentService).getUnpaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10));
    }

    @Test
    void getPaid() throws Exception {
        paymentDTO.setStatus(Status.PAID);
        PaymentPageDTO expectedPage = new PaymentPageDTO(
                List.of(paymentDTO),
                1,
                1,
                10,
                0
        );
        when(paymentService.getUnpaid(paymentDTO.getPassengerId(), PageRequest.of(0,10)))
                .thenReturn(expectedPage);

        mockMvc.perform(get("/api/v1/payment/unpaid/{passengerId}", paymentDTO.getPassengerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payments[0].passengerId").value(paymentDTO.getPassengerId()))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(paymentService).getUnpaid(paymentDTO.getPassengerId(), PageRequest.of(0, 10));
    }

    @Test
    void softDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/payment/{passengerId}", paymentDTO.getPassengerId()))
                .andExpect(status().isNoContent());

        verify(paymentService).softDelete(paymentDTO.getPassengerId());
    }
}