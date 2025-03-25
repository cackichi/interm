package org.example.controllers;

import org.example.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/passenger")
    public ResponseEntity<ErrorResponse> passengerFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Сервис пассажиров временно недоступен. Попробуйте позже."));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ErrorResponse> driverFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Сервис водителей временно недоступен. Попробуйте позже."));
    }

    @GetMapping("/rides")
    public ResponseEntity<ErrorResponse> ridesFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Сервис поездок временно недоступен. Попробуйте позже."));
    }

    @GetMapping("/payment")
    public ResponseEntity<ErrorResponse> paymentFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Платежный сервис временно недоступен. Попробуйте позже."));
    }

    @GetMapping("/rating")
    public ResponseEntity<ErrorResponse> ratingFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Сервис рейтингов временно недоступен. Попробуйте позже."));
    }
}
