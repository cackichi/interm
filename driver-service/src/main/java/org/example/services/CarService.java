package org.example.services;

import org.example.collections.Car;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.exceptions.NotFoundException;
import org.springframework.data.domain.Pageable;

public interface CarService {
    void create(String driverId, CarDTO carDTO);
    CarDTO findCar(String id, String number) throws NotFoundException;
    CarPageDTO findCars(String driverId, Pageable pageable);
    CarDTO mapToDTO(Car car);
    Car mapToCar(CarDTO carDTO);
    void updateCar(String driverId, CarDTO carDTO);
    void removeCarFromDriver(String driverId, String number);
}
