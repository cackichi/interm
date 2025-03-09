package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.entities.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Сущность платежа")
public class PaymentDTO {
    @Schema(description = "Идентификатор")
    private Long id;
    @Schema(description = "Идентификатор пассажира")
    private Long passengerId;
    @Schema(description = "Идентификатор поездки")
    private Long rideId;
    @Schema(description = "Стоимость")
    private double cost;
    @Schema(description = "Статус оплаты")
    private Status status;
    @Schema(description = "Статус доступности")
    private boolean deleted = false;
}