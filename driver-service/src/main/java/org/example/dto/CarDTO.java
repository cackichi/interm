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
