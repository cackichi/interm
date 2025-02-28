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
public class RidePageDTO {
    private List<RideDTO> rides;
    private int totalElem;
    private int totalPages;
    private int size;
    private int number;
}
