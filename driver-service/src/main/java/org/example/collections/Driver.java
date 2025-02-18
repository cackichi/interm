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
@Document(collection = "driver")
public class Driver {
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("experience")
    private int experience;
    @Field("phone")
    private String phone;
    @Field("email")
    private String email;

    public Driver(String name, int experience, String phone, String email) {
        this.name = name;
        this.experience = experience;
        this.phone = phone;
        this.email = email;
    }
}
