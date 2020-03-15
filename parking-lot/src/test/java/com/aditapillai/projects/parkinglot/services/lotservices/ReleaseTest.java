package com.aditapillai.projects.parkinglot.services.lotservices;

import com.aditapillai.projects.parkinglot.dao.CarDao;
import com.aditapillai.projects.parkinglot.dao.SlotDao;
import com.aditapillai.projects.parkinglot.models.Car;
import com.aditapillai.projects.parkinglot.services.LotService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class ReleaseTest {
    @InjectMocks
    private LotService service;
    @Mock
    private SlotDao slotDao;
    @Mock
    private CarDao carDao;

    @Test
    public void releaseTest() {
        String registrationNumber = "KA01AG1111";
        Mockito.when(this.slotDao.unallocateSlotFor(Mockito.eq(registrationNumber)))
               .thenReturn(Mono.just(true));
        Car expected = new Car("KA01AG1111,RED,CHEV");
        Mockito.when(this.carDao.delete(Mockito.eq(registrationNumber)))
               .thenReturn(Mono.just(expected));

        Assert.assertEquals(expected, this.service.release(registrationNumber)
                                                  .block());

    }

    @Test
    public void releaseInvalidCar() {
        boolean result = false;
        try {
            this.service.release("ABC")
                        .block();
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid Registration number: ABC", e.getMessage());
            result = true;
        }
        Assert.assertTrue(result);

    }

}
