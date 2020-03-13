package com.aditapillai.projects.parkinglot.services;

import com.aditapillai.projects.parkinglot.dao.CarDao;
import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.models.Slot;
import com.aditapillai.projects.parkinglot.utils.LotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class LotService {
    private SlotDao slotDao;
    private CarDao carDao;
    private CacheService cache;
    @Value("${lot.slots-per-level:10}")
    private int maxSlotsPerLevel;

    /**
     * Allocate a parking slot for the incoming car with the given registration number
     *
     * @param registrationNumber the registration number of the car
     * @return the allocated slot
     */
    public Mono<Slot> allocate(String registrationNumber) {
        int number = LotUtils.getNumber(registrationNumber);

        return this.slotDao.findOccupiedSlots(number)
                           .flatMap(this::getAvailableSlot)
                           .doOnNext(slot -> this.cache.setUnavailable(slot.getSlotNumber(), slot.getLevel()))
                           .flatMap(slot -> this.slotDao.bookSlot(slot, registrationNumber, number)
                                                        .doOnError(error -> this.cache.setAvailable(slot.getSlotNumber()
                                                                , slot.getLevel())));

    }

    /**
     * Fetch an available slot to book for the current car
     *
     * @param slots all the occupied slots that are of interest
     * @return an available slot
     */
    private Mono<Slot> getAvailableSlot(List<Slot> slots) {
        Slot availableSlot = slots.stream()
                                  .flatMap(this::getAvailableNeighbors)
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Slots not available"));
        return Mono.just(availableSlot);
    }

    /**
     * Return the available slots that are immediately next to the provided slot
     *
     * @param slot the occupied slot whose neighbors need to be checked and returned if available
     * @return Stream of available slots
     */
    private Stream<Slot> getAvailableNeighbors(Slot slot) {
        int level = slot.getLevel();
        int slotNumber = slot.getSlotNumber();

        int prevSlotNumber = slotNumber - 1;
        int nextSlotNumber = slotNumber + 1;
        Slot prevSlot = null;
        Slot nextSlot = null;

        if (prevSlotNumber > 0) {
            prevSlot = this.cache.isAvailable(prevSlotNumber, level)
                                 .filter(result -> result)
                                 .doOnNext(result -> this.cache.setUnavailable(prevSlotNumber, level))
                                 .flatMap(result -> this.slotDao.findSlot(prevSlotNumber, level))
                                 .doOnError(error -> this.cache.setAvailable(prevSlotNumber, level))
                                 .block();

        }

        if (nextSlotNumber <= maxSlotsPerLevel) {
            nextSlot = this.cache.isAvailable(nextSlotNumber, level)
                                 .filter(result -> result)
                                 .doOnNext(result -> this.cache.setUnavailable(nextSlotNumber, level))
                                 .flatMap(result -> this.slotDao.findSlot(nextSlotNumber, level))
                                 .doOnError(error -> this.cache.setAvailable(nextSlotNumber, level))
                                 .block();
        }

        return Stream.of(prevSlot, nextSlot)
                     .filter(Objects::nonNull);
    }

    /**
     * Register the car into the lot
     *
     * @param car incoming car
     * @return the car post a successful registration
     */
    public Mono<Car> register(Car car) {
        String registrationNumber = car.getRegistrationNumber();

        if (!LotUtils.registrationNumberPattern.asPredicate()
                                               .test(registrationNumber)) {
            return Mono.error(() -> new RuntimeException(String.format("Invalid Registration number: %s", registrationNumber)));
        }
        return this.carDao.save(car);
    }

    /**
     * Release a car from the parking lot
     *
     * @param registrationNumber the registration number of the car
     * @return the car that was released
     */
    public Mono<Car> release(String registrationNumber) {
        if (!LotUtils.registrationNumberPattern.asPredicate()
                                               .test(registrationNumber)) {
            return Mono.error(() -> new RuntimeException(String.format("Invalid Registration number: %s", registrationNumber)));
        }
        return this.slotDao.unallocateSlotFor(registrationNumber)
                           .flatMap(noResult -> this.carDao.delete(registrationNumber));
    }

    @Autowired
    public void setSlotDao(SlotDao slotDao) {
        this.slotDao = slotDao;
    }

    @Autowired
    public void setCarDao(CarDao carDao) {
        this.carDao = carDao;
    }

    @Autowired
    public void setCache(CacheService cache) {
        this.cache = cache;
    }
}
