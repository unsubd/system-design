package com.aditapillai.projects.ttmm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "splits")
@AllArgsConstructor
@Getter
public class Split {
    private final String billId;
    private final List<Share> shares;
}
