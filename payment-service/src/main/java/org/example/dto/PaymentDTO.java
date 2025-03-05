package org.example.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.entities.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDTO {
    private Long id;
    private Long passengerId;
    private Long rideId;
    private double cost;
    private Status status;
    private boolean deleted = false;
}