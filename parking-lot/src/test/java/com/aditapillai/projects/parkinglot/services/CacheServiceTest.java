package com.aditapillai.projects.parkinglot.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {
    @Test
    public void allTests() {
        CacheService cacheService = new CacheService();
        Assert.assertTrue(cacheService.setUnavailable(1, 1));
        Assert.assertFalse(cacheService.isAvailable(1, 1));
        Assert.assertTrue(cacheService.setAvailable(1, 1));
        Assert.assertTrue(cacheService.isAvailable(1, 1));
    }
}
