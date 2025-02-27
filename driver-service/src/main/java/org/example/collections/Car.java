package org.example.collections;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "car")
public class Car {
    @Id
    private String id;
    @Field("brand")
    private String brand;
    @Field("color")
    private String color;
    @Field("driverId")
    private String driverId;
    @Field("deleted")
    private boolean deleted = false;

    public Car(String brand, String color, String driverId) {
        this.brand = brand;
        this.color = color;
        this.driverId = driverId;
    }
}
