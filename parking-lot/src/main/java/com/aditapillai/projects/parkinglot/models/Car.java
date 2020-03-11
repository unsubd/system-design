package com.aditapillai.projects.parkinglot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "cars")
public class Car {
    private String registrationNumber;
    private String color;
    private String manufacturer;
    @Id
    private String id;
    @JsonIgnore
    private String _class;

    public Car(String input) {
        String[] split = input.split(",");
        this.registrationNumber = split[0];
        this.color = split[1];
        this.manufacturer = split[2];
    }

    public Car() {
    }

}
