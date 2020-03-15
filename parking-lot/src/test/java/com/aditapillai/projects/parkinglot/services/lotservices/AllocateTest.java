package com.aditapillai.projects.parkinglot.services.lotservices;

import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Slot;
import com.aditapillai.projects.parkinglot.services.CacheService;
import com.aditapillai.projects.parkinglot.services.LotService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AllocateTest {

    @InjectMocks
    private LotService service;
    @Mock
    private SlotDao slotDao;

    private CacheService cacheService = new CacheService();

    @Before
    public void setUp() {
        this.service.setLevels(3);
        this.service.setMaxSlotsPerLevel(2);
        this.service.setCache(cacheService);
    }

    @Test
    public void allocateToNewFloor() {
        this.cacheService.setUnavailable(1, 1);
        this.cacheService.setUnavailable(2, 1);

        Mockito.when(this.slotDao.findOccupiedSlots(Mockito.anyInt()))
               .thenReturn(Mono.just(new LinkedList<>()));

        Slot expected = new Slot();
        expected.setLevel(3);
        expected.setSlotNumber(1);
        expected.setAvailable(false);

        Mockito.when(this.slotDao.findSlot(Mockito.eq(1), Mockito.eq(3)))
               .thenReturn(Mono.just(expected));
        Mockito.when(this.slotDao.bookSlot(Mockito.eq(expected), Mockito.eq("KA01AG2001"), Mockito.eq(2001)))
               .thenReturn(Mono.just(expected));

        Slot allocatedSlot = this.service.allocate("KA01AG2001")
                                         .block();

        Assert.assertEquals(expected, allocatedSlot);
        Assert.assertFalse(this.cacheService.isAvailable(1, 3));

    }

    @Test
    public void allocateFirstCar() {
        Slot expected = new Slot();
        expected.setLevel(1);
        expected.setSlotNumber(1);
        Mockito.when(this.slotDao.findSlot(Mockito.eq(1), Mockito.eq(1)))
               .thenReturn(Mono.just(expected));
        Mockito.when(this.slotDao.bookSlot(Mockito.eq(expected), Mockito.anyString(), Mockito.anyInt()))
               .thenReturn(Mono.just(expected));
        Mockito.when(this.slotDao.findOccupiedSlots(Mockito.anyInt()))
               .thenReturn(Mono.just(new LinkedList<>()));

        Slot allocatedSlot = this.service.allocate("KA01AG1000")
                                         .block();

        Assert.assertEquals(expected, allocatedSlot);
        Assert.assertFalse(this.cacheService.isAvailable(1, 1));

        this.cacheService.setAvailable(1, 1);

        allocatedSlot = this.service.allocate("KA01AG1001")
                                    .block();
        Assert.assertEquals(expected, allocatedSlot);
        Assert.assertFalse(this.cacheService.isAvailable(1, 1));
    }

    @Test
    public void allSlotsBooked() {
        List<Slot> slots = new LinkedList<>();
        int levels = 3;
        int maxSlotsPerLevel = 2;
        for (int level = 1; level <= levels; level++) {
            for (int slot = 1; slot <= maxSlotsPerLevel; slot++) {
                Slot s = new Slot();
                s.setSlotNumber(slot);
                s.setLevel(level);
                slots.add(s);
                this.cacheService.setUnavailable(slot, level);
            }
        }

        Mockito.when(this.slotDao.findOccupiedSlots(Mockito.anyInt()))
               .thenReturn(Mono.just(slots));
        boolean result = false;

        try {
            this.service.allocate("KA01AG1000")
                        .block();

        } catch (RuntimeException e) {
            Assert.assertEquals("All slots booked!", e.getMessage());
            result = true;
        }

        Assert.assertTrue(result);
    }

    @Test
    public void noSlotsLeft() {
        List<Slot> slots = new LinkedList<>();
        int levels = 3;
        int maxSlotsPerLevel = 2;
        for (int level = 1; level <= levels; level++) {
            for (int slot = 1; slot <= maxSlotsPerLevel; slot++) {
                this.cacheService.setUnavailable(slot, level);
            }
        }

        Mockito.when(this.slotDao.findOccupiedSlots(Mockito.anyInt()))
               .thenReturn(Mono.just(slots));
        boolean result = false;

        try {
            this.service.allocate("KA01AG1000")
                        .block();

        } catch (RuntimeException e) {
            Assert.assertEquals("No available slots", e.getMessage());
            result = true;
        }

        Assert.assertTrue(result);
    }

}