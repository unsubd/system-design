package com.aditapillai.projects.parkinglot.services;

import com.aditapillai.projects.parkinglot.dao.CarDao;
import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.models.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LotService {
    private SlotDao slotDao;
    private CarDao carDao;

    public Mono<Slot> allocate(String registrationNumber) {
        return null;
    }

    public Mono<Car> register(Car car) {
        return this.carDao.save(car);
    }

    public Mono<Car> release(String registrationNumber) {
        return null;
    }

    @Autowired
    public void setSlotDao(SlotDao slotDao) {
        this.slotDao = slotDao;
    }

    @Autowired
    public void setCarDao(CarDao carDao) {
        this.carDao = carDao;
    }
}
