package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Passenger {
    @Id
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean deleted = false;
}
