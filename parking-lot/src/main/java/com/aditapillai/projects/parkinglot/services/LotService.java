package com.aditapillai.projects.parkinglot.services;

import com.aditapillai.projects.parkinglot.dao.CarDao;
import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.models.Slot;
import com.aditapillai.projects.parkinglot.utils.LotUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
public class LotService {
    private SlotDao slotDao;
    private CarDao carDao;
    private CacheService cache;

    /**
     * Allocate a parking slot for the incoming car with the given registration number
     *
     * @param registrationNumber the registration number of the car
     * @return the allocated slot
     */
    public Mono<Slot> allocate(String registrationNumber) {
        int number = LotUtils.getNumber(registrationNumber);

        return this.slotDao.findOccupiedSlots(number)
                           .flatMap(slots -> this.getAvailableSlot(slots, registrationNumber, number))
                           .doOnNext(slot -> this.cache.setUnavailable(slot.getSlotNumber(), slot.getLevel()))
                           .flatMap(slot -> this.slotDao.bookSlot(slot, registrationNumber, number));
    }

    /**
     * Fetch an available slot to book for the current car
     *
     * @param slots              all the occupied slots that are of interest
     * @param registrationNumber the registration number of the car
     * @param number             numeric number of the car
     * @return an available slot
     */
    private Mono<Slot> getAvailableSlot(List<Slot> slots, String registrationNumber, int number) {
        boolean isOdd = LotUtils.isOdd(number);
        Predicate<Slot> levelFilter = slot -> !isOdd || LotUtils.isOdd(slot.getLevel());

        Slot availableSlot = slots.stream()
                                  .filter(levelFilter)
                                  .flatMap(this::getAvailableSlots)
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("Slots not available"));
        return this.slotDao.bookSlot(availableSlot, registrationNumber, number)
                           .doOnError(error -> this.cache.setAvailable(availableSlot.getSlotNumber(), availableSlot.getLevel()));
    }

    /**
     * Return the available slots that are immediately next to the provided slot
     *
     * @param slot the occupied slot whose neighbors need to be checked and returned if available
     * @return Stream of available slots
     */
    private Stream<Slot> getAvailableSlots(Slot slot) {
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
                                 .flatMap(noResult -> this.slotDao.findSlot(prevSlotNumber, level))
                                 .doOnError(error -> this.cache.setAvailable(prevSlotNumber, level))
                                 .block();

        }

        if (nextSlotNumber < 10) {
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
