package com.aditapillai.projects.parkinglot.dao;

import com.aditapillai.projects.parkinglot.models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CarDao {
    private ReactiveMongoOperations mongoOperations;

    public Mono<Car> save(Car car) {
        return this.mongoOperations.save(car);
    }

    public Mono<Car> delete(String registrationNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("registrationNumber")
                                  .is(registrationNumber));
        return this.mongoOperations.findAndRemove(query, Car.class);
    }

    @Autowired
    public void setMongoOperations(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
