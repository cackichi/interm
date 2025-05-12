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
@Schema(description = "Обертка DTO списка машин с пагинацией")
public class CarPageDTO {
    @Schema(description = "Список DTO машин")
    private List<CarDTO> cars;
    @Schema(description = "Количество машин")
    private long totalElements;
    @Schema(description = "Фактическое количество страниц")
    private int totalPages;
    @Schema(description = "Размер пагинации")
    private int size;
    @Schema(description = "Номер страницы")
    private int number;
}
