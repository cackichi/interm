package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassengerDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean deleted;
}
