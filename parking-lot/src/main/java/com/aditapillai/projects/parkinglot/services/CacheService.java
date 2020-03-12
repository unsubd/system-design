package com.aditapillai.projects.parkinglot.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
     * @return Reactive boolean value with true if the slot is available.
     */
    public Mono<Boolean> isAvailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        return Mono.just(this.cache.getOrDefault(key, true));
    }

    /**
     * Block a slot
     *
     * @param slotNumber of the slot
     * @param level      of the slot
     */
    public void setUnavailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        this.cache.put(key, false);
    }

    /**
     * Unlbock a slot
     *
     * @param slotNumber of the slot
     * @param level      of the slot
     */
    public void setAvailable(int slotNumber, int level) {
        String key = String.format("%d_%d", slotNumber, level);
        this.cache.remove(key);
    }

}
