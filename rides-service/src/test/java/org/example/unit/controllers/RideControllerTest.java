package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.RideController;
import org.example.dto.RideDTO;
import org.example.dto.RidePageDTO;
import org.example.entities.Status;
import org.example.services.RideServiceImpl;
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

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RideControllerTest {
    @Mock
    private RideServiceImpl rideService;
    @InjectMocks
    private RideController rideController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RideDTO rideDTO;
    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
        objectMapper = new ObjectMapper();
        rideDTO = new RideDTO(
                1L,
                1L,
                "1",
                "Moskow",
                "Petersburg",
                Status.WAITING,
                false
        );
    }
    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/v1/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isCreated());

        verify(rideService).create(rideDTO);
    }
    @Test
    void findById() throws Exception {
        when(rideService.findById(rideDTO.getId()))
                .thenReturn(rideDTO);
        mockMvc.perform(get("/api/v1/rides/{id}", rideDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rideDTO.getId()))
                .andExpect(jsonPath("$.passengerId").value(rideDTO.getPassengerId()))
                .andExpect(jsonPath("$.driverId").value(rideDTO.getDriverId()))
                .andExpect(jsonPath("$.pointA").value(rideDTO.getPointA()))
                .andExpect(jsonPath("$.pointB").value(rideDTO.getPointB()));
    }
    @Test
    void edit() throws Exception {
        mockMvc.perform(patch("/api/v1/rides/{id}", rideDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rideDTO)))
                .andExpect(status().isNoContent());
        verify(rideService).update(rideDTO.getId(), rideDTO);
    }
    @Test
    void softDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/rides/{id}", rideDTO.getId()))
                .andExpect(status().isNoContent());
        verify(rideService).softDelete(rideDTO.getId());
    }
    @Test
    void findAll() throws Exception {
        when(rideService.findAllNotDeleted(PageRequest.of(0, 10)))
                .thenReturn(new RidePageDTO(new ArrayList<>(), 0, 0, 10, 0));

        mockMvc.perform(get("/api/v1/rides/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElem").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }
    @Test
    void driverStartTravel() throws Exception {
        mockMvc.perform(patch("/api/v1/rides/start/{id}", rideDTO.getDriverId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Поездка найдена, запрос на проверку id водителя отправлен"));
        verify(rideService).checkFreeRide(rideDTO.getDriverId());
    }
    @Test
    void driverStopTravel() throws Exception {
        mockMvc.perform(patch("/api/v1/rides/stop/{id}?rating=3&cost=55", rideDTO.getDriverId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Процесс завершения поездки начат"));
    }
}
