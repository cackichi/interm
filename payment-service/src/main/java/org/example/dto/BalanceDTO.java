package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Сущность баланса")
public class BalanceDTO {
    @Schema(description = "Идентификатор пассажира")
    private Long passengerId;
    @Schema(description = "Текущий баланс")
    private double balance;
    @Schema(description = "Время последнего пополнения")
    private LocalDateTime timeLastDeposit;
    @Schema(description = "Статус доступности")
    private boolean deleted = false;
}
