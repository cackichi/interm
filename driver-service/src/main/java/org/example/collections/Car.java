package org.example.collections;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Car {
    @Id
    private String number;
    @Field("brand")
    private String brand;
    @Field("color")
    private String color;
    @Field("deleted")
    private boolean deleted = false;

    public Car(String brand, String color) {
        this.brand = brand;
        this.color = color;
    }
}
