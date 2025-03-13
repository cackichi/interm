package org.example.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.example.dto.ErrorResponse;
import org.example.exceptions.InvalidRatingException;
import org.example.exceptions.NegativeCostException;
import org.example.exceptions.NoWaitingRideException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RideExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(NoWaitingRideException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundWaiting(NoWaitingRideException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRating(InvalidRatingException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(NegativeCostException.class)
    public ResponseEntity<ErrorResponse> handleNegativeCost(NegativeCostException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
    }
}
