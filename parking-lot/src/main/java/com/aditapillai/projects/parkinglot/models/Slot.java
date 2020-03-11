package com.aditapillai.projects.parkinglot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "slots")
public class Slot {
    private boolean available;
    private int number;
    private int level;
    private Car car;
    @Id
    private String id;
    @JsonIgnore
    private String _class;

}
