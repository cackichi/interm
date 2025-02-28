package org.example.services;

import lombok.AllArgsConstructor;
import org.example.collections.Driver;
import org.example.dto.DriverDTO;
import org.example.dto.DriverPageDTO;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;

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
        driverRepository.softDelete(id);
    }

    @Transactional
    public void update(String id, DriverDTO driverDTO){
        Optional<Driver> driverOptional = driverRepository.findById(id);
        Driver existingDriver = driverOptional.orElseThrow();
        modelMapper.map(driverDTO, existingDriver);
        driverRepository.save(existingDriver);
    }

    public DriverPageDTO findAllNotDeleted(Pageable pageable){
        Page<Driver> driversPage = driverRepository.findAllNotDeleted(pageable);

        List<DriverDTO> driverDTOs = driversPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new DriverPageDTO(
                driverDTOs,
                driversPage.getTotalElements(),
                driversPage.getTotalPages(),
                pageable.getPageSize(),
                pageable.getPageNumber()
        );
    }

    public DriverDTO findById(String id) throws NotFoundException{
        return driverRepository.findById(id).map(this::mapToDTO).orElseThrow(() -> new NotFoundException("Такой водитель не найден"));
    }

    @Transactional
    public void hardDelete(String id){
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
