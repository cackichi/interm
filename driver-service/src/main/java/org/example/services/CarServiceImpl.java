package org.example.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.collections.Car;
import org.example.collections.Driver;
import org.example.dto.CarDTO;
import org.example.dto.CarPageDTO;
import org.example.exceptions.NotFoundException;
import org.example.repositories.DriverRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {
    private final DriverRepository driverRepository;
    private final ModelMapper modelMapper;
    private final MongoTemplate mongoTemplate;

    @Transactional
    @Override
    public void removeCarFromDriver(String driverId, String number) {
        log.info("Removing car {} from driver {}", number, driverId);
        Query query = Query.query(Criteria.where("_id").is(driverId));
        Update update = new Update();
        update.pull("cars", Query.query(Criteria.where("number").is(number)));

        mongoTemplate.updateFirst(query, update, Driver.class);
        log.debug("Car {} removed from driver {}", number, driverId);
    }

    @Transactional
    @Override
    public void updateCar(String driverId, CarDTO carDTO) {
        log.info("Updating car {} for driver {}", carDTO.getNumber(), driverId);
        Query query = Query.query(
                new Criteria().andOperator(
                        Criteria.where("_id").is(driverId),
                        Criteria.where("cars.number").is(carDTO.getNumber())
                )
        );
        Update update = new Update();
        if (carDTO.getBrand() != null) {
            update.set("cars.$.brand", carDTO.getBrand());
        }
        if (carDTO.getColor() != null) {
            update.set("cars.$.color", carDTO.getColor());
        }
        mongoTemplate.updateFirst(query, update, Driver.class);
        log.debug("Car {} updated for driver {}", carDTO.getNumber(), driverId);
    }

    @Override
    public Car mapToCar(CarDTO carDTO) {
        return modelMapper.map(carDTO, Car.class);
    }

    @Override
    public CarDTO mapToDTO(Car car) {
        return modelMapper.map(car, CarDTO.class);
    }

    @Override
    public CarPageDTO findCars(String driverId, Pageable pageable) {
        log.debug("Finding cars for driver {}, page {}, size {}",
                driverId, pageable.getPageNumber(), pageable.getPageSize());

        Driver driver = driverRepository.findCarsById(driverId);
        if (driver == null || driver.getCars() == null) {
            log.warn("No cars found for driver {}", driverId);
            return new CarPageDTO(List.of(), 0, 0, pageable.getPageSize(), pageable.getPageNumber());
        }

        List<Car> cars = driver.getCars();
        int totalCars = cars.size();
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), totalCars);

        List<Car> carsPage = cars.subList(start, end);

        List<CarDTO> carDTOs = carsPage.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        log.debug("Found {} cars for driver {}", carDTOs.size(), driverId);
        return new CarPageDTO(carDTOs, totalCars,
                (int) Math.ceil((double) totalCars / pageable.getPageSize()),
                pageable.getPageSize(), pageable.getPageNumber());
    }

    @Override
    public CarDTO findCar(String id, String number) throws NotFoundException {
        log.debug("Looking for car {} of driver {}", number, id);
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Driver not found with ID: {}", id);
                    return new NotFoundException("Водитель с таким айди не найден");
                });

        return driver.getCars().stream()
                .filter(car -> car.getNumber().equals(number))
                .map(this::mapToDTO)
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Car not found with number: {} for driver {}", number, id);
                    return new NotFoundException("Машина с таким номером не найден");
                });
    }

    @Override
    public void create(String driverId, CarDTO carDTO) {
        log.info("Creating new car {} for driver {}", carDTO.getNumber(), driverId);
        Car car = mapToCar(carDTO);
        Query query = Query.query(
                new Criteria().andOperator(
                        Criteria.where("_id").is(driverId)
                )
        );

        Update update = new Update();
        update.addToSet("cars", car);

        mongoTemplate.updateFirst(query, update, Driver.class);
        log.debug("Car {} created for driver {}", carDTO.getNumber(), driverId);
    }
}