package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
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
