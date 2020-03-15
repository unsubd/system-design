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
    private int maxSlotsPerLevel;
    private int levels;

    /**
     * Allocate a parking slot for the incoming car with the given registration number
     *
     * @param registrationNumber the registration number of the car
     * @return the allocated slot
     */
    public Mono<Slot> allocate(String registrationNumber) {
        final String registrationNumberUpperCase = registrationNumber.toUpperCase();
        int number = LotUtils.getNumber(registrationNumber);

        return this.slotDao.findOccupiedSlots(number)
                           .flatMap(slots -> this.getAvailableSlot(slots, number))
                           .flatMap(slot -> this.slotDao.bookSlot(slot, registrationNumberUpperCase, number)
                                                        .doOnError(error -> this.cache.setAvailable(slot.getSlotNumber()
                                                                , slot.getLevel())));

    }

    /**
     * Fetch an available slot to book for the current car
     *
     * @param slots  all the occupied slots that are of interest
     * @param number numeric registration number of the car
     * @return an available slot
     */
    private Mono<Slot> getAvailableSlot(List<Slot> slots, int number) {
        if (slots.isEmpty()) {
            return this.getFirstApplicableSlot(number);
        }

        Slot availableSlot = slots.stream()
                                  .flatMap(slot -> this.getAvailableNeighbors(slot.getSlotNumber(), slot.getLevel()))
                                  .findFirst()
                                  .orElseThrow(() -> new RuntimeException("All slots booked!"));

        return Mono.just(availableSlot);
    }

    /**
     * Return the first applicable slot for the given vehicle number
     *
     * @param number numeric registration number of the car
     * @return the applicable slot, or return an error
     */
    private Mono<Slot> getFirstApplicableSlot(int number) {
        int incr = LotUtils.isOdd(number) ? 2: 1;

        for (int level = 1; level <= levels; level += incr) {
            for (int slot = 1; slot <= maxSlotsPerLevel; slot++) {
                if (this.cache.isAvailable(slot, level)) {
                    if (this.cache.setUnavailable(slot, level)) {
                        final int slotNumber = slot;
                        final int levelNumber = level;

                        return this.slotDao.findSlot(slot, level)
                                           .doOnError(error -> this.cache.setAvailable(slotNumber, levelNumber));
                    }
                }
            }
        }

        return Mono.error(() -> new RuntimeException("No available slots"));
    }

    /**
     * Return the available slots that are immediately next to the provided slot
     *
     * @param slotNumber current slot number whose neighbors need to be checked
     * @param level      the current level under consideration
     * @return Stream of available slots
     */
    private Stream<Slot> getAvailableNeighbors(int slotNumber, int level) {
        int prevSlotNumber = slotNumber - 1;
        int nextSlotNumber = slotNumber + 1;
        Slot prevSlot = null;
        Slot nextSlot = null;

        if (prevSlotNumber > 0) {
            prevSlot = Mono.just(this.cache.isAvailable(prevSlotNumber, level))
                           .filter(result -> result)
                           .doOnNext(result -> this.cache.setUnavailable(prevSlotNumber, level))
                           .flatMap(result -> this.slotDao.findSlot(prevSlotNumber, level))
                           .doOnError(error -> this.cache.setAvailable(prevSlotNumber, level))
                           .block();

        }

        if (nextSlotNumber <= maxSlotsPerLevel) {
            nextSlot = Mono.just(this.cache.isAvailable(nextSlotNumber, level))
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
        String registrationNumberUpperCase = registrationNumber.toUpperCase();
        if (!LotUtils.registrationNumberPattern.asPredicate()
                                               .test(registrationNumber)) {
            return Mono.error(() -> new RuntimeException(String.format("Invalid Registration number: %s", registrationNumber)));
        }
        return this.slotDao.unallocateSlotFor(registrationNumber)
                           .flatMap(noResult -> this.carDao.delete(registrationNumberUpperCase));
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

    @Value("${lot.levels:5}")
    public void setLevels(int levels) {
        this.levels = levels;
    }

    @Value("${lot.slots-per-level:10}")
    public void setMaxSlotsPerLevel(int maxSlotsPerLevel) {
        this.maxSlotsPerLevel = maxSlotsPerLevel;
    }
}
