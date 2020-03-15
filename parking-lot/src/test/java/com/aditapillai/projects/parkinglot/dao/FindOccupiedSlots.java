package com.aditapillai.projects.parkinglot.dao;

import com.aditapillai.projects.parkinglot.models.Slot;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.aditapillai.projects.parkinglot.dao.LookupKeys.AVAILABLE;
import static com.aditapillai.projects.parkinglot.dao.LookupKeys.LEVEL;

@RunWith(MockitoJUnitRunner.class)
public class FindOccupiedSlots {
    @InjectMocks
    private SlotDao dao;
    @Mock
    private ReactiveMongoOperations mongoOperations;

    @Before
    public void setUp() {
        this.dao.setMaxSlotsPerLevel(4);
        this.dao.setLevels(5);
    }

    @Test
    public void findOccupiedSlots() {
        int maxSlots = 4;
        Slot slot = new Slot();
        slot.setLevel(3);
        slot.setSlotNumber(4);
        slot.setVehicleNumber(1000);

        Slot slot2 = new Slot();
        slot2.setLevel(3);
        slot2.setSlotNumber(1);
        slot2.setVehicleNumber(998);

        Slot slot3 = new Slot();
        slot3.setLevel(5);
        slot3.setSlotNumber(1);
        slot3.setVehicleNumber(1002);


        List<Slot> slots = this.getFilledLevel(1, maxSlots);
        slots.add(slot2);
        slots.add(slot);
        slots.add(slot3);

        int carNumber = 1001;
        Query query = new Query();
        Criteria criteria = Criteria.where(AVAILABLE)
                                    .is(false);
        criteria.and(LEVEL)
                .mod(2, 1);
        query.addCriteria(criteria);

        Mockito.when(this.mongoOperations.find(Mockito.eq(query), Mockito.eq(Slot.class)))
               .thenReturn(Flux.fromIterable(slots));

        List<Slot> result = this.dao.findOccupiedSlots(carNumber)
                                    .block();

        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(slot, result.get(0));
        Assert.assertEquals(slot2, result.get(1));
        Assert.assertEquals(slot3, result.get(2));

    }

    private List<Slot> getFilledLevel(int level, int maxLevels) {
        return IntStream.rangeClosed(level, maxLevels)
                        .mapToObj(index -> {
                            Slot slot = new Slot();
                            slot.setLevel(1);
                            slot.setSlotNumber(index);
                            slot.setAvailable(false);
                            return slot;
                        })
                        .collect(Collectors.toList());
    }
}
