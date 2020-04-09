package com.aditapillai.projects.ttmm.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "bills")
@RequiredArgsConstructor
@Getter
public class Bill {
    private final double amount;
    private final String authorId;
    private final List<String> users;

    @Id
    private final String id = ObjectId.get()
                                      .toHexString();
}
