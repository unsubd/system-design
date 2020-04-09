package com.aditapillai.projects.ttmm.services.balance;

import com.aditapillai.projects.ttmm.dao.BalanceDao;
import com.aditapillai.projects.ttmm.models.User;
import com.aditapillai.projects.ttmm.services.BalanceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SettleUpTest {
    @InjectMocks
    private BalanceService service;
    @Mock
    private BalanceDao dao;

    @Test
    public void settleUpTest() {
        Mockito.when(this.dao.updateBalances(Mockito.anyList()))
               .thenReturn(true);
        boolean result = this.service.settleUp(User.builder()
                                                   .id("1")
                                                   .build(), User.builder()
                                                                 .id("2")
                                                                 .build());
        Assert.assertTrue(result);
    }
}
