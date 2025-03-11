package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Passenger;
import org.example.entities.Status;
import org.example.services.PassengerServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PassengerControllerTest {
    @Mock
    private PassengerServiceImpl passengerService;
    @InjectMocks
    private PassengerController passengerController;
    private MockMvc mockMvc;
    PassengerDTO passengerDTO;
    Passenger passenger;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(passengerController).build();
        passenger = new Passenger(
                1L,
                "John Doe",
                "john@email.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        passengerDTO = new PassengerDTO(
                1L,
                "John Doe",
                "john@email.com",
                "+79999999999",
                false,
                Status.NOT_ACTIVE
        );
        objectMapper = new ObjectMapper();
    }
    @Test
    void createPassenger() throws Exception {
        when(passengerService.save(any(PassengerDTO.class)))
                .thenReturn(passenger);
        String json = objectMapper.writeValueAsString(passengerDTO);
        mockMvc.perform(post("/api/v1/passenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }
    @Test
    void editPassenger() throws Exception {
        String json = objectMapper.writeValueAsString(passengerDTO);
        mockMvc.perform(patch("/api/v1/passenger/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }
    @Test
    void softDeletePassenger() throws Exception {
        mockMvc.perform(delete("/api/v1/passenger/{id}", 1L))
                .andExpect(status().isNoContent());
    }
    @Test
    void findAll() throws Exception{
        List<PassengerDTO> passengers = List.of(passengerDTO);
        when(passengerService.findAllNotDeleted(PageRequest.of(0, 10)))
                .thenReturn(new PassengerPageDTO(passengers, 1L, 1, 10,0));
        mockMvc.perform(get("/api/v1/passenger"))
                .andExpect(jsonPath("$.totalElements").value(1L))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }
    @Test
    void findOne() throws Exception{
        when(passengerService.findOne(1L))
                .thenReturn(passengerDTO);
        mockMvc.perform(get("/api/v1/passenger/{id}", 1L))
                .andExpect(jsonPath("$.id").value(passengerDTO.getId()))
                .andExpect(jsonPath("$.name").value(passengerDTO.getName()))
                .andExpect(jsonPath("$.email").value(passengerDTO.getEmail()))
                .andExpect(jsonPath("$.phoneNumber").value(passengerDTO.getPhoneNumber()))
                .andExpect(jsonPath("$.status").value(passengerDTO.getStatus().toString()))
                .andExpect(jsonPath("$.deleted").value(passengerDTO.isDeleted()));
    }

    @Test
    void orderTaxi() throws Exception {
        when(passengerService.checkExistsAndStatus(1L)).thenReturn(true);
        mockMvc.perform(patch("/api/v1/passenger/order-taxi/{passengerId}?pointA=Shmidta&pointB=Simonova", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Запрос на создание заявки на поездку отправлен, ожидайте пока водитель примет ее"));
    }
}
