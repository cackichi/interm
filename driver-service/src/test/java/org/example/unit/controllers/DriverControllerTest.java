package org.example.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controllers.DriverController;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.services.CarServiceImpl;
import org.example.services.DriverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DriverControllerTest {
    @InjectMocks
    private DriverController driverController;
    @Mock
    private DriverServiceImpl driverService;
    @Mock
    private CarServiceImpl carService;
    private ObjectMapper objectMapper;
    private DriverDTO driverDTO;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        driverDTO = new DriverDTO(
                "1",
                "Radrigo",
                3,
                "80445345698",
                "example@gmail.com",
                false,
                "FREE",
                new ArrayList<>()
        );
        mockMvc = MockMvcBuilders.standaloneSetup(driverController).build();
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isCreated());
        verify(driverService).create(driverDTO);
    }

    @Test
    void edit() throws Exception {
        mockMvc.perform(patch("/api/v1/drivers/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isOk());

        verify(driverService).update("1" , driverDTO);
    }

    @Test
    void deleteDriver() throws Exception {
        mockMvc.perform(delete("/api/v1/drivers/{id}", "1"))
                .andExpect(status().isNoContent());
        verify(driverService).softDelete("1");
    }

    @Test
    void findDrivers() throws Exception {
        DriverPageDTO driverPageDTO = new DriverPageDTO(
                List.of(driverDTO),
                1,
                1,
                10,
                0
        );
        when(driverService.findAllNotDeleted(any(Pageable.class))).thenReturn(driverPageDTO);

        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(driverService).findAllNotDeleted(any(Pageable.class));
    }

    @Test
    void findDriver() throws Exception {
        when(driverService.findById(driverDTO.getId())).thenReturn(driverDTO);

        mockMvc.perform(get("/api/v1/drivers/{id}", driverDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(driverDTO.getId()))
                .andExpect(jsonPath("$.name").value(driverDTO.getName()));

        verify(driverService).findById(driverDTO.getId());
    }
    @Test
    void findCarsOfDriver() throws Exception {
        CarPageDTO carPageDTO = new CarPageDTO(
                List.of(new CarDTO()),
                1,
                1,
                10,
                0
        );
        when(carService.findCars(anyString(), any(Pageable.class))).thenReturn(carPageDTO);

        mockMvc.perform(get("/api/v1/drivers/{id}/cars", driverDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
        verify(carService).findCars(driverDTO.getId(), PageRequest.of(0, 10));
    }
    @Test
    void deleteCar() throws Exception {
        mockMvc.perform(delete("/api/v1/drivers/{id}/car/{numbed}", driverDTO.getId(),"7456"))
                .andExpect(status().isNoContent());

        verify(carService).removeCarFromDriver(driverDTO.getId(),"7456");
    }
    @Test
    void findCar() throws Exception {
        CarDTO carDTO = new CarDTO("7456", "AUDI", "black", false);
        when(carService.findCar(anyString(), anyString())).thenReturn(carDTO);

        mockMvc.perform(get("/api/v1/drivers/{id}/car/{numbed}", driverDTO.getId(),"7456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("7456"))
                .andExpect(jsonPath("$.brand").value("AUDI"))
                .andExpect(jsonPath("$.color").value("black"));
    }
    @Test
    void createCar() throws Exception {
        CarDTO carDTO = new CarDTO("7456", "AUDI", "black", false);
        mockMvc.perform(post("/api/v1/drivers/{id}/car", driverDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTO)))
                .andExpect(status().isCreated());

        verify(carService).create(driverDTO.getId(), carDTO);
    }
    @Test
    void updateCar() throws Exception {
        mockMvc.perform(patch("/api/v1/drivers/{id}/car", driverDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CarDTO())))
                .andExpect(status().isNoContent());
    }
}