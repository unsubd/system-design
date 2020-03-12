package com.aditapillai.projects.parkinglot.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "slots")
public class Slot {
    private boolean available;
    private int slotNumber;
    private int level;
    private String registrationNumber;
    private int vehicleNumber;
    @Id
    private String id;

}
