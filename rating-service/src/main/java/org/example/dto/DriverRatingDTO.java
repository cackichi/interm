package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Сущность рейтинга водителя")
public class DriverRatingDTO {
    @Schema(description = "Идентификатор водителя")
    private String driverId;
    @Schema(description = "Средний рейтинг")
    private double averageRating;
    @Schema(description = "Количество оценок")
    private int ratingCount;
    @Schema(description = "Статус доступности")
    private boolean deleted = false;
}
