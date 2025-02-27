package org.example.services;

import lombok.AllArgsConstructor;
import org.example.collections.Car;
import org.example.dto.CarDTO;
import org.example.repositories.CarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;

    public Car mapToCar(CarDTO carDTO){
        return modelMapper.map(carDTO, Car.class);
    }

    public CarDTO mapToDTO(Car car){
        return modelMapper.map(car, CarDTO.class);
    }

    public void createCar(CarDTO carDTO, String driverId){
        Car car = mapToCar(carDTO);
        car.setDriverId(driverId);
        carRepository.save(car);
    }

    @Transactional
    public void softDelete(String driverId){
        carRepository.softDelete(driverId);
    }

    @Transactional
    public void update(String id, CarDTO carDTO){
        Car car = mapToCar(carDTO);
        carRepository.update(id, car.getBrand(), car.getColor());
    }

    public List<Car> findAllNotDeleted(){
        return carRepository.findAllNotDeleted();
    }

    @Transactional
    public void hardDelete(String id){
        carRepository.deleteById(id);
    }
}
