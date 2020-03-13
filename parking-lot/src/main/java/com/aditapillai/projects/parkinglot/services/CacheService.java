package com.aditapillai.projects.parkinglot.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {
    private Map<String, Boolean> cache = new ConcurrentHashMap<>();

    /**
     * Check whether a slot is occupied
     *
     * @param slotNumber of the slot
     * @param level      of the slot
     * @return true if the slot is available
     */
    public boolean isAvailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        return this.cache.getOrDefault(key, true);
    }

    /**
     * Block a slot
     *
     * @param slotNumber of the slot
     * @param level      of the slot
     * @return true if the value was set successfully
     */
    public boolean setUnavailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        this.cache.put(key, false);
        return true;
    }

    /**
     * Unlbock a slot
     *
     * @param slotNumber of the slot
     * @param level      of the slot
     * @return true if the value was set successfully
     */
    public boolean setAvailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        this.cache.remove(key);
        return true;
    }

}
