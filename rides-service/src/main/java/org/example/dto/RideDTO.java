package org.example.dto;
import lombok.*;
import org.example.entities.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RideDTO {
    private Long id;
    private Long passengerId;
    private String driverId;
    private String pointA;
    private String pointB;
    private Status status;
    private boolean deleted;
}
