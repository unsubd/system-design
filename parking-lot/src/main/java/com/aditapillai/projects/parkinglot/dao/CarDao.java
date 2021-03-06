package com.aditapillai.projects.parkinglot.dao;

import com.aditapillai.projects.parkinglot.models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.aditapillai.projects.parkinglot.dao.LookupKeys.REGISTRATION_NUMBER;

@Service
public class CarDao {
    private ReactiveMongoOperations mongoOperations;

    /**
     * Save the car into the DB
     *
     * @param car to be saved
     * @return the saved car as stored in the DB
     */
    public Mono<Car> save(Car car) {
        return this.mongoOperations.save(car);
    }

    /**
     * Delete a car from the DB
     *
     * @param carRegistrationNumber unique registration number of the car
     * @return the deleted car
     */
    public Mono<Car> delete(String carRegistrationNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where(REGISTRATION_NUMBER)
                                  .is(carRegistrationNumber));
        return this.mongoOperations.findAndRemove(query, Car.class);
    }

    @Autowired
    public void setMongoOperations(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
