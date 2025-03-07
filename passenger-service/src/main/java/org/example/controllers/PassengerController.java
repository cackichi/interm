package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.ErrorResponse;
import org.example.dto.PassengerDTO;
import org.example.dto.PassengerPageDTO;
import org.example.entities.Passenger;
import org.example.exceptions.OrderTaxiException;
import org.example.services.PassengerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passenger")
@AllArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerDTO> createPassenger(@RequestBody PassengerDTO passengerDTO) {
        Passenger passenger = passengerService.save(passengerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.mapToDTO(passenger));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ErrorResponse> editPassenger(@RequestBody PassengerDTO edit, @PathVariable("id") Long id) {
        passengerService.updatePass(id, edit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> softDeletePassenger(@PathVariable("id") Long id) {
        passengerService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PassengerPageDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "0") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(passengerService.findAllNotDeleted(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDTO> findOne(@PathVariable("id") Long id) {
        return ResponseEntity.ok(passengerService.findOne(id));
    }

    @PatchMapping("/order-taxi/{passengerId}")
    public ResponseEntity<ErrorResponse> orderTaxi(@PathVariable("passengerId") Long id) throws OrderTaxiException {
        if (!passengerService.checkExistsAndStatus(id))
            throw new OrderTaxiException("Пассажира с таким айди не существует либо он уже в поездке");
        passengerService.orderTaxi(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Запрос на создание заявки на поездку отправлен, ожидайте пока водитель примет ее"));
    }
}
