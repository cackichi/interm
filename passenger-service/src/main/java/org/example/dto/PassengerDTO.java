package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.entities.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Schema(description = "Сущность пассажира")
public class PassengerDTO {
    @Schema(description = "Идентификатор")
    private Long id;
    @Schema(description = "ФИО")
    private String name;
    @Schema(description = "Адрес эл.почты")
    private String email;
    @Schema(description = "Номер телефона")
    private String phoneNumber;
    @Schema(description = "Статус доступности")
    private boolean deleted;
    @Schema(description = "Статус относительно поездки")
    private Status status;

    public PassengerDTO(Long id) {
        this.id = id;
    }
}
