package org.example.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.entities.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Schema(description = "Сущность поездки")
public class RideDTO {
    @Schema(description = "Идентификатор")
    private Long id;
    @Schema(description = "Идентификатор пассажира")
    private Long passengerId;
    @Schema(description = "Идентификатор водителя")
    private String driverId;
    @Schema(description = "Пункт отправления")
    private String pointA;
    @Schema(description = "Пункт назначения")
    private String pointB;
    @Schema(description = "Статус поездки")
    private Status status;
    @Schema(description = "Статус доступности")
    private boolean deleted;
}
