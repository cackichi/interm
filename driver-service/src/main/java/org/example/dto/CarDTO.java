package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Schema(description = "Сущность машины")
public class CarDTO {
    @Schema(description = "Рег. номер")
    private String number;
    @Schema(description = "Бренд")
    private String brand;
    @Schema(description = "Цвет")
    private String color;
    @Schema(description = "Статус доступности")
    private boolean deleted;
}
