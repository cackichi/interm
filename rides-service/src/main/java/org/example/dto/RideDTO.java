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
public class RideDTO {
    private Long id;
    private Long passengerId;
    private Long driverId;
    private String pointA;
    private String pointB;
    private Status status;
}
