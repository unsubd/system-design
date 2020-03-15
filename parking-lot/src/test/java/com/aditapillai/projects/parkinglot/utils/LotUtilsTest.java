package com.aditapillai.projects.parkinglot.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LotUtilsTest {
    @Test
    public void testRegistrationPattern() {
        Assert.assertFalse(LotUtils.registrationNumberPattern.asPredicate()
                                                             .test("KA01AG11111"));
        Assert.assertTrue(LotUtils.registrationNumberPattern.asPredicate()
                                                            .test("KA01AG1111"));
        Assert.assertEquals(1111, LotUtils.getNumber("KA01AG1111"));
        Assert.assertFalse(LotUtils.isOdd(2));
        Assert.assertTrue(LotUtils.isOdd(1));
    }
}
