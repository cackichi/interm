package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользовательский контроллер", description = "Взаимодействие с пассажирами")
public class PassengerController {
    private final PassengerService passengerService;

    @PostMapping
    @Operation(summary = "Создание пассажира", description = "Позволяет создать нового пассажира")
    public ResponseEntity<PassengerDTO> createPassenger(
            @RequestBody @Parameter(required = true) PassengerDTO passengerDTO
    ) {
        Passenger passenger = passengerService.save(passengerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(passengerService.mapToDTO(passenger));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновление пассажира", description = "Позволяет обновить информацию о пассажире")
    public ResponseEntity<ErrorResponse> editPassenger(
            @RequestBody @Parameter(required = true) PassengerDTO edit,
            @PathVariable("id") @Parameter(description = "id обновляемого пассажира", required = true) Long id
    ) {
        passengerService.updatePass(id, edit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Мягкое удаление пассажира", description = "Позволяет мягко удалить пассажира")
    public ResponseEntity<ErrorResponse> softDeletePassenger(
            @PathVariable("id") @Parameter(description = "id удаляемого пассажира", required = true) Long id
    ) {
        passengerService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Список пассажиров с пагинацией", description = "Позволяет найти всех пассажиров + информация пагинации")
    public ResponseEntity<PassengerPageDTO> findAll(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Номер страницы пагинации") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Размер пагинации") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(passengerService.findAllNotDeleted(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск пассажира", description = "Позволяет найти суествующего пассажира")
    public ResponseEntity<PassengerDTO> findOne(
            @PathVariable("id") @Parameter(description = "Id необходимого пассажира", required = true) Long id
    ) {
        return ResponseEntity.ok(passengerService.findOne(id));
    }

    @PatchMapping("/order-taxi/{passengerId}")
    @Operation(summary = "Заказ такси", description = "Позволяет отправить запрос на заказ такси")
    public ResponseEntity<ErrorResponse> orderTaxi(
            @PathVariable("passengerId") @Parameter(description = "Id пассажира который заказывает такси", required = true) Long id
    ) throws OrderTaxiException {
        if (!passengerService.checkExistsAndStatus(id))
            throw new OrderTaxiException("Пассажира с таким айди не существует либо он уже в поездке");
        passengerService.orderTaxi(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ErrorResponse("Запрос на создание заявки на поездку отправлен, ожидайте пока водитель примет ее"));
    }
}
