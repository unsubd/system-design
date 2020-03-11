package com.aditapillai.projects.parkinglot.models;

import lombok.Getter;

@Getter
public class Car {
    private final String number;
    private final String color;
    private final String manufacturer;

    public Car(String input) {
        String[] split = input.split(",");
        this.number = split[0];
        this.color = split[1];
        this.manufacturer = split[2];
    }
}
