package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private String id;
    private String name;
    private int experience;
    private String phone;
    private String email;
    private boolean deleted = false;
}
