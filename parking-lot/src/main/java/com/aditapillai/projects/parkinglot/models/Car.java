package com.aditapillai.projects.parkinglot.models;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "cars")
public class Car {
    @NonNull
    private String registrationNumber;
    private String color;
    private String manufacturer;
    @Id
    private String id;

    public Car(String input) {
        String[] split = input.split(",");
        this.registrationNumber = split[0].toUpperCase();
        this.color = split[1];
        this.manufacturer = split[2];
    }

    public Car() {
    }

}
