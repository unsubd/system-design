package com.aditapillai.projects.parkinglot.dao;

import com.aditapillai.projects.parkinglot.models.Slot;
import com.aditapillai.projects.parkinglot.utils.LotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Service
public class SlotDao {
    private ReactiveMongoOperations mongoOperations;

    @Autowired
    public void setMongoOperations(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    /**
     * Free up the space for the given car registration number
     *
     * @param carRegistrationNumber of the car for which the parking space needs to be freed
     */
    public Mono<Void> unallocateSlotFor(String carRegistrationNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("registrationNumber")
                                  .is(carRegistrationNumber));
        Update update = new Update();
        update.unset("registrationNumber")
              .set("available", true)
              .unset("vehicleNumber");

        return this.mongoOperations.updateFirst(query, update, Slot.class)
                                   .flatMap(result -> Mono.empty());
    }

    /**
     * Fetch all the occupied slots in the parking lot based on the following criteria.
     * Odd numbered cars can only go to odd floors while even can go anywhere
     * Sort the result on level and absolute difference between car number and parked car number
     *
     * @param carNumber car number that has to be parked
     * @return occupied slots that are applicable for the given car number
     */
    public Mono<List<Slot>> findOccupiedSlots(int carNumber) {
        Query query = new Query();
        Criteria criteria = Criteria.where("available")
                                    .is(false);
        boolean isOdd = LotUtils.isOdd(carNumber);
        if (isOdd) {
            criteria.and("level")
                    .mod(2, 1);
        }
        query.addCriteria(criteria);
        return this.mongoOperations.find(query, Slot.class)
                                   .collectSortedList(Comparator.comparingInt(Slot::getLevel)
                                                                .thenComparingInt(slot -> Math.abs(slot.getVehicleNumber() - carNumber)));
    }

    /**
     * Book a slot for the car
     *
     * @param slot               Slot to be booked
     * @param registrationNumber registration number of the car
     * @param number             numeric number of the car
     * @return Booked slot
     */
    public Mono<Slot> bookSlot(Slot slot, String registrationNumber, int number) {
        slot.setAvailable(false);
        slot.setRegistrationNumber(registrationNumber);

        Update update = new Update();
        update.set("available", slot.isAvailable())
              .set("registrationNumber", registrationNumber)
              .set("vehicleNumber", number)
              .set("slotNumber", slot.getSlotNumber())
              .set("level", slot.getLevel());

        Query query = new Query();
        query.addCriteria(Criteria.where("slotNumber")
                                  .is(slot.getSlotNumber())
                                  .and("level")
                                  .is(slot.getLevel()));

        return this.mongoOperations.upsert(query, update, Slot.class)
                                   .thenReturn(slot);
    }

    /**
     * Find the slot with the given slot number and level
     *
     * @param slotNumber slot number
     * @param level      level
     * @return the slot from db
     */
    public Mono<Slot> findSlot(int slotNumber, int level) {
        Query query = new Query();
        query.addCriteria(Criteria.where("slotNumber")
                                  .is(slotNumber)
                                  .and("level")
                                  .is(level));
        Slot slot = new Slot();
        slot.setAvailable(true);
        slot.setLevel(level);
        slot.setSlotNumber(slotNumber);

        return this.mongoOperations.findOne(query, Slot.class)
                                   .defaultIfEmpty(slot);
    }
}
