package org.example.services;

import lombok.AllArgsConstructor;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        driver.setStatus("FREE");
        driverRepository.save(driver);
    }

    @Transactional
    public void softDelete(String id){
        carService.softDelete(id);
        driverRepository.softDelete(id);
    }

    @Transactional
    public void update(String id, DriverDTO driverDTO){
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow();
        modelMapper.map(driverDTO, existingDriver);
        driverRepository.save(existingDriver);
    }

    public List<Driver> findAllNotDeleted(){
        return driverRepository.findAllNotDeleted();
    }

    @Transactional
    public void hardDelete(String id){
        carService.hardDelete(id);
        driverRepository.deleteById(id);
    }

    //Отправляем запрос на получение свободной поездки
    // (если такая имеется то обратно вернется true и статус водителя изменится на BUSY)
    // возможно чтобы вернуть уведомление пользователю использовать feign
    void getFreeRide(){

    }

    // Водитель заканчивает поездку; указывает цену за нее и оценку пассажиру
    // Отсюда идет запрос в сервис поездки чтобы указать ее статус как завершенной
    // Потом в сервис оплаты для установления задолженности
    // Потом в сервис рейтинга для установки рейтинга пассажиру
    void stopTraveling(){

    }
}
