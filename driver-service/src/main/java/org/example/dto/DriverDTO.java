package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.collections.Car;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private String id;
    private String name;
    private Integer experience;
    private String phone;
    private String email;
    private boolean deleted = false;
    private String status;
    private List<Car> cars;
}
