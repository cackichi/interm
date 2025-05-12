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
@Schema(description = "Обертка DTO над списком поездок с пагинацией")
public class RidePageDTO {
    @Schema(description = "Список поездок")
    private List<RideDTO> rides;
    @Schema(description = "Количество поездок")
    private int totalElem;
    @Schema(description = "Фактическое количество страниц")
    private int totalPages;
    @Schema(description = "Размер пагинации")
    private int size;
    @Schema(description = "Номер страницы")
    private int number;
}
