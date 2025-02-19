package org.example.services;

import lombok.AllArgsConstructor;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final CarService carService;

    public Driver mapToDriver(DriverDTO driverDTO){
        return modelMapper.map(driverDTO, Driver.class);
    }

    public DriverDTO mapToDTO(Driver driver){
        return modelMapper.map(driver, DriverDTO.class);
    }

    public void create(DriverDTO driverDTO){
        Driver driver = mapToDriver(driverDTO);
        driverRepository.save(driver);
    }

    @Transactional
    public void softDelete(String id){
        carService.softDelete(id);
        driverRepository.softDelete(id);
    }

    @Transactional
    public void update(String id, DriverDTO driverDTO){
        Driver driver = mapToDriver(driverDTO);
        driverRepository.update(id, driver.getName(), driver.getExperience(), driver.getPhone(), driver.getEmail());
    }

    public List<Driver> findAllNotDeleted(){
        return driverRepository.findAllNotDeleted();
    }
}
