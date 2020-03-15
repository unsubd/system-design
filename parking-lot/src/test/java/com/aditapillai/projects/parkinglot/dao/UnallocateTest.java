package com.aditapillai.projects.parkinglot.dao;

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
public class UnallocateTest {
    @InjectMocks
    private SlotDao dao;
    @Mock
    private ReactiveMongoOperations mongoOperations;

    @Before
    public void setUp() {
        this.dao.setLevels(3);
        this.dao.setMaxSlotsPerLevel(2);
    }

    @Test
    public void unallocate() {
        UpdateResult updateResult = UpdateResult.acknowledged(1, 1L, new BsonBoolean(true));
        String carRegistrationNumber = "KA01AG1000";

        Query query = new Query();
        query.addCriteria(Criteria.where(REGISTRATION_NUMBER)
                                  .is(carRegistrationNumber));
        Update update = new Update();
        update.unset(REGISTRATION_NUMBER)
              .set(AVAILABLE, true)
              .unset(VEHICLE_NUMBER);
        Mockito.when(this.mongoOperations.updateFirst(Mockito.eq(query), Mockito.eq(update), Mockito.eq(Slot.class)))
               .thenReturn(Mono.just(updateResult));

        Boolean result = this.dao.unallocateSlotFor(carRegistrationNumber)
                                 .block();

        Assert.assertNotNull(result);
        Assert.assertTrue(result);
    }
}
