package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entities.Status;

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
    private Status status;
}
