package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exceptions.NullIdException;
import org.example.services.CarServiceImpl;
import org.example.services.DriverServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@AllArgsConstructor
@Tag(name = "Пользовательский контроллер", description = "Взаимодействие с водителями")
public class DriverController {
    private final DriverServiceImpl driverServiceImpl;
    private final CarServiceImpl carServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание водителя", description = "Позволяет создать нового водителя")
    public ResponseEntity<HttpStatus> create(
            @RequestBody @Parameter(required = true) DriverDTO driverDTO
    ) throws NullIdException {
        if(driverDTO.getId() == null) throw new NullIdException("Вы не указали id водителя");
        driverServiceImpl.create(driverDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Обновление водителя", description = "Позволяет обновить существующего водителя")
    public ResponseEntity<HttpStatus> edit(
            @RequestBody @Parameter(required = true) DriverDTO driverDTO,
            @PathVariable("id") @Parameter(description = "id обновляемого водителя", required = true) String id
    ) throws NotFoundException {
        driverServiceImpl.update(String.valueOf(id), driverDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Мягкое удаление водителя", description = "Позволяет мягко удалить водителя")
    public ResponseEntity<HttpStatus> delete(
            @PathVariable("id") @Parameter(description = "id удаляемого водителя", required = true) String id
    ){
        driverServiceImpl.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Список водителей", description = "Позволяет получить список водителей с пагинацией")
    public ResponseEntity<DriverPageDTO> findDrivers(
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы пагинации") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Размер пагинации") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        DriverPageDTO driverPageDTO = driverServiceImpl.findAllNotDeleted(pageable);
        return ResponseEntity.ok(driverPageDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск водителя", description = "Позволяет найти существующего водителя")
    public ResponseEntity<DriverDTO> findDriver(
            @PathVariable("id") @Parameter(description = "id необходимого водителя", required = true) String id
    ) throws NotFoundException {
        return ResponseEntity.ok(driverServiceImpl.findById(id));
    }

    @GetMapping("/{id}/cars")
    @Operation(summary = "Поиск машин", description = "Позволяет найти список машин конкретного водителя с пагинацией")
    public ResponseEntity<CarPageDTO> findCarsOfDriver(
            @PathVariable("id") @Parameter(description = "id необходимого водителя", required = true) String driverId,
            @RequestParam(defaultValue = "0") @Parameter(description = "Номер страницы пагинации") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Размер пагинации") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        CarPageDTO carPageDTO = carServiceImpl.findCars(driverId, pageable);
        return ResponseEntity.ok(carPageDTO);
    }

    @DeleteMapping("/{driverId}/car/{number}")
    @Operation(summary = "Удаление машины", description = "Позволяет удалить машину конкретного водителя")
    public ResponseEntity<HttpStatus> deleteCar(
            @PathVariable("driverId") @Parameter(description = "id необходимого водителя", required = true) String id,
            @PathVariable("number") @Parameter(description = "number удаляемой машины", required = true) String number
    ){
        carServiceImpl.removeCarFromDriver(id, number);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{driverId}/car/{number}")
    @Operation(summary = "Поиск машины", description = "Позволяет найти конкретную машину у конкретного водителя")
    public ResponseEntity<CarDTO> findCar(
            @PathVariable("driverId") @Parameter(description = "id необходимого водителя", required = true) String id,
            @PathVariable("number") @Parameter(description = "number необходимой машины", required = true) String number
    ) throws NotFoundException {
        return ResponseEntity.ok(carServiceImpl.findCar(id, number));
    }

    @PostMapping("/{driverId}/car")
    @Operation(summary = "Создание машины", description = "Позволяет добавить машину конкретному водителю")
    public ResponseEntity<HttpStatus> createCar(
            @PathVariable("driverId") @Parameter(description = "id необходимого водителя", required = true) String driverId,
            @RequestBody @Parameter(required = true) CarDTO carDTO
    ) throws NullIdException {
        if(carDTO.getNumber() == null) throw new NullIdException("Вы не указали номер машины");
        carServiceImpl.create(driverId, carDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{driverId}/car")
    @Operation(summary = "Обновление машины", description = "Позволяет обновить машину конкретного водителя")
    public ResponseEntity<HttpStatus> updateCar(
            @PathVariable("driverId") @Parameter(description = "id необходимого водителя", required = true) String driverId,
            @RequestBody @Parameter(required = true) CarDTO carDTO
    ) {
        carServiceImpl.updateCar(driverId, carDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
