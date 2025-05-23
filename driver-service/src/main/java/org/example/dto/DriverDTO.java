package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.collections.Car;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Сущность водителя")
public class DriverDTO {
    @Schema(description = "Идентификатор")
    private String id;
    @Schema(description = "ФИО")
    private String name;
    @Schema(description = "Стаж")
    private Integer experience;
    @Schema(description = "Номер телефона")
    private String phone;
    @Schema(description = "Адрес эл.почты")
    private String email;
    @Schema(description = "Статус достпуности")
    private boolean deleted = false;
    @Schema(description = "Статус относительно поездки")
    private String status;
    @Schema(description = "Машины")
    @EqualsAndHashCode.Exclude
    private List<Car> cars;

    public DriverDTO(String id) {
        this.id = id;
    }
}
