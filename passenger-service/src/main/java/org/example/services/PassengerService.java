package org.example.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.PassengerServiceApplication;
import org.example.dto.PassengerDTO;
import org.example.entities.Passenger;
import org.example.repositories.PassengerRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PassengerService {
    private final PassengerRepo passengerRepo;
    private final ModelMapper modelMapper;

    public PassengerDTO mapToDTO(Passenger passenger){
        return modelMapper.map(passenger, PassengerDTO.class);
    }

    public Passenger mapToPass(PassengerDTO passengerDTO){
        return modelMapper.map(passengerDTO, Passenger.class);
    }

    public Passenger save(PassengerDTO passengerDTO){
        return  passengerRepo.save(mapToPass(passengerDTO));
    }

    @Transactional
    public void softDelete(Long id){
        passengerRepo.softDelete(id);
    }

    @Transactional
    public void updatePass(Long id, PassengerDTO passengerDTO){
        passengerRepo.editData(id, passengerDTO.getName(), passengerDTO.getEmail(), passengerDTO.getPhoneNumber());
    }

    @Transactional
    public void hardDelete(Long id){
        passengerRepo.deleteById(id);
    }

    public List<Passenger> findAllNotDeleted(){
        return passengerRepo.findAllNotDeleted();
    }

    //Функция для отправки запроса в сервис поездок
    //(меняем статуc пассажира на WAITING и создаем в сервисе поездок запись со статусов FREE)
    public void orderTaxi(){

    }
    //Запрос в сервис оплаты(можно заказать новое такси только если все оплачено)(Возможно feign чтобы оповестить пассажира)
    public void checkDebt(){

    }

    //Запрос в сервис оплаты(Оплата задолженности + прикрепляем рейтинг за поездку)
    public void pay(){

    }
}
