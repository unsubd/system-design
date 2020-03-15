package com.aditapillai.projects.parkinglot.dao.slotdao;

import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Slot;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonBoolean;
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
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import static com.aditapillai.projects.parkinglot.dao.LookupKeys.*;

@RunWith(MockitoJUnitRunner.class)
public class BookTest {
    @InjectMocks
    private SlotDao dao;
    @Mock
    private ReactiveMongoOperations mongoOperations;

    @Before
    public void setUp() {
        this.dao.setLevels(3);
        this.dao.setMaxSlotsPerLevel(4);
    }

    @Test
    public void bookSlotTest() {
        String regNumber = "KA01AG1100";
        int num = 1100;
        Slot slot = new Slot();
        slot.setLevel(1);
        slot.setSlotNumber(1);
        slot.setAvailable(true);

        Update update = new Update();
        update.set(AVAILABLE, false)
              .set(REGISTRATION_NUMBER, regNumber)
              .set(VEHICLE_NUMBER, num)
              .set(SLOT_NUMBER, 1)
              .set(LEVEL, 1);

        Query query = new Query();
        query.addCriteria(Criteria.where(SLOT_NUMBER)
                                  .is(1)
                                  .and(LEVEL)
                                  .is(1));

        UpdateResult updateResult = UpdateResult.acknowledged(1, 1L, new BsonBoolean(true));

        Mockito.when(this.mongoOperations.upsert(Mockito.eq(query), Mockito.eq(update), Mockito.eq(Slot.class)))
               .thenReturn(Mono.just(updateResult));
        Slot result = this.dao.bookSlot(slot, regNumber, num)
                              .block();

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getLevel());
        Assert.assertEquals(1, result.getSlotNumber());
    }
}
