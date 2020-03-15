package com.aditapillai.projects.parkinglot.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return slotNumber == slot.slotNumber &&
                level == slot.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slotNumber, level);
    }
}
