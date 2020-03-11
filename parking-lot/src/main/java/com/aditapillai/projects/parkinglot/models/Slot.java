package com.aditapillai.projects.parkinglot.models;

import lombok.Data;

@Data
public class Slot {
    private boolean available;
    private int number;
    private int level;
    private Car car;
}
