package com.aditapillai.projects.parkinglot.services;

import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.models.Slot;
import org.springframework.stereotype.Service;

@Service
public class LotService {

    public Slot allocate(String registrationNumber) {
        return null;
    }

    public boolean register(Car car) {
        return false;
    }

    public boolean release(String registrationNumber) {
        return false;
    }
}
