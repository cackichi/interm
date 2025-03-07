package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "DTO обертка над List с пагинацией")
public class PassengerPageDTO {
    @Schema(description = "Список DTO пассажиров")
    private List<PassengerDTO> passengers;
    @Schema(description = "Количество пассажиров")
    private long totalElements;
    @Schema(description = "Фактическое количество страниц")
    private int totalPages;
    @Schema(description = "Размер пагинации")
    private int size;
    @Schema(description = "Номер страницы пагинации")
    private int number;
}
