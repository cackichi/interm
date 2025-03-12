package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
@Schema(description = "Сущность рейтинга пассажира")
public class PassengerRatingDTO {
    @Schema(description = "Идентификатор пассажира")
    private Long passengerId;
    @Schema(description = "Средний рейтинг")
    private double averageRating;
    @Schema(description = "Количетсво оценок")
    private int ratingCount;
    @Schema(description = "Статус доступности")
    private boolean deleted = false;
}
