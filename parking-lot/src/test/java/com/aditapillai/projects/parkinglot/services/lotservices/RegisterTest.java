package com.aditapillai.projects.parkinglot.services.lotservices;

import com.aditapillai.projects.parkinglot.dao.CarDao;
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
public class RegisterTest {
    @InjectMocks
    private LotService service;

    @Mock
    private CarDao carDao;

    @Test
    public void registerInvalidCarNumber() {
        Car car = new Car("KA01AG11111,RED,CHEV");
        boolean result = false;
        try {
            this.service.register(car)
                        .block();
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid Registration number: KA01AG11111", e.getMessage());
            result = true;
        }

        Assert.assertTrue(result);
    }

    @Test
    public void registerValidCarNumber() {
        Car car = new Car("KA01AG1111,RED,CHEV");
        Mockito.when(this.carDao.save(Mockito.eq(car)))
               .thenReturn(Mono.just(car));

        Car registeredCar = this.service.register(car)
                                        .block();

        Assert.assertEquals(car, registeredCar);

    }

}
