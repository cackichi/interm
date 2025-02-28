package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarPageDTO {
    private List<CarDTO> cars;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
}
