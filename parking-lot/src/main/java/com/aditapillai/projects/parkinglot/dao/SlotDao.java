package com.aditapillai.projects.parkinglot.dao;

import com.aditapillai.projects.parkinglot.models.Slot;
import com.aditapillai.projects.parkinglot.utils.LotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.aditapillai.projects.parkinglot.dao.LookupKeys.*;

@Service
public class SlotDao {
    private ReactiveMongoOperations mongoOperations;
    private int levels;
    private int maxSlotsPerLevel;

    @Autowired
    public void setMongoOperations(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    /**
     * Free up the space for the given car registration number
     *
     * @param carRegistrationNumber of the car for which the parking space needs to be freed
     */
    public Mono<Boolean> unallocateSlotFor(String carRegistrationNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where(REGISTRATION_NUMBER)
                                  .is(carRegistrationNumber));
        Update update = new Update();
        update.unset(REGISTRATION_NUMBER)
              .set(AVAILABLE, true)
              .unset(VEHICLE_NUMBER);

        return this.mongoOperations.updateFirst(query, update, Slot.class)
                                   .map(result -> true);
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
        Criteria criteria = Criteria.where(AVAILABLE)
                                    .is(false);
        boolean isOdd = LotUtils.isOdd(carNumber);
        if (isOdd) {
            criteria.and(LEVEL)
                    .mod(2, 1);
        }
        query.addCriteria(criteria);
        //TODO: Move convert to aggregate
        return this.mongoOperations.find(query, Slot.class)
                                   .collect(Collectors.groupingBy(Slot::getLevel))
                                   .map(map -> map.entrySet()
                                                  .stream()
                                                  .filter(entry -> entry.getValue()
                                                                        .size() < maxSlotsPerLevel)
                                                  .flatMap(entry -> entry.getValue()
                                                                         .stream())
                                                  .sorted(Comparator.comparingInt(Slot::getLevel)
                                                                    .thenComparingInt(slot -> Math.abs(slot.getVehicleNumber() - carNumber)))
                                                  .collect(Collectors.toList()));
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
        slot.setVehicleNumber(number);

        Update update = new Update();
        update.set(AVAILABLE, slot.isAvailable())
              .set(REGISTRATION_NUMBER, registrationNumber)
              .set(VEHICLE_NUMBER, number)
              .set(SLOT_NUMBER, slot.getSlotNumber())
              .set(LEVEL, slot.getLevel());

        Query query = new Query();
        query.addCriteria(Criteria.where(SLOT_NUMBER)
                                  .is(slot.getSlotNumber())
                                  .and(LEVEL)
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
        query.addCriteria(Criteria.where(SLOT_NUMBER)
                                  .is(slotNumber)
                                  .and(LEVEL)
                                  .is(level));
        Slot slot = new Slot();
        slot.setAvailable(true);
        slot.setLevel(level);
        slot.setSlotNumber(slotNumber);

        return this.mongoOperations.findOne(query, Slot.class)
                                   .defaultIfEmpty(slot);
    }

    @Value("${lot.levels:5}")
    public void setLevels(int levels) {
        this.levels = levels;
    }

    @Value("${lot.slots-per-level:10}")
    public void setMaxSlotsPerLevel(int maxSlotsPerLevel) {
        this.maxSlotsPerLevel = maxSlotsPerLevel;
    }
}
