package com.aditapillai.projects.parkinglot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Service;

@Service
public class SlotDao {
    private ReactiveMongoOperations mongoOperations;

    @Autowired
    public void setMongoOperations(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }


}
