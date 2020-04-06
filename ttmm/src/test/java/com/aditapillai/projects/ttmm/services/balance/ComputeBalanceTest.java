package com.aditapillai.projects.ttmm.services.balance;

import com.aditapillai.projects.ttmm.models.Balance;
import com.aditapillai.projects.ttmm.models.Share;
import com.aditapillai.projects.ttmm.models.Split;
import com.aditapillai.projects.ttmm.services.BalanceService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ComputeBalanceTest {

    private final BalanceService service = new BalanceService();

    @Test
    public void computeBalanceWhenSomeoneDidNotPay() {
        List<Share> shares = new ArrayList<>();
        shares.add(new Share("1", 100, 100));
        shares.add(new Share("2", 0, 100));
        shares.add(new Share("3", 0, 100));
        shares.add(new Share("4", 300, 100));

        List<Balance> balances = service.computeBalancesFromSplit(new Split("id", shares));
        Assert.assertNotNull(balances);
        Assert.assertEquals(2, balances.size());

        Balance b1 = new Balance("4", "2", 100);
        Balance b2 = new Balance("4", "3", 100);

        List<Balance> expectedBalances = List.of(b1, b2);
        expectedBalances.forEach(balance -> Assert.assertTrue(balances.contains(balance)));
    }

    @Test
    public void computeBalanceWhenSomeonePaidPartialAmount() {
        List<Share> shares = new ArrayList<>();
        shares.add(new Share("1", 150, 100));
        shares.add(new Share("2", 50, 100));
        shares.add(new Share("3", 50, 100));
        shares.add(new Share("4", 150, 100));

        List<Balance> balances = service.computeBalancesFromSplit(new Split("id", shares));
        Assert.assertNotNull(balances);
        Assert.assertEquals(2, balances.size());

        Balance b1 = new Balance("1", "2", 50);
        Balance b2 = new Balance("4", "3", 50);

        List<Balance> expectedBalances = List.of(b1, b2);
        expectedBalances.forEach(balance -> Assert.assertTrue(balances.contains(balance)));
    }

    @Test
    public void computeBalanceWhenSomeonePaidPartialAmountButOwesToMultiple() {
        List<Share> shares = new ArrayList<>();
        shares.add(new Share("1", 140, 100));
        shares.add(new Share("2", 50, 100));
        shares.add(new Share("3", 70, 100));
        shares.add(new Share("4", 140, 100));

        List<Balance> balances = service.computeBalancesFromSplit(new Split("id", shares));
        Assert.assertNotNull(balances);
        Assert.assertEquals(3, balances.size());

        Balance b1 = new Balance("1", "2", 40);
        Balance b2 = new Balance("4", "2", 10);
        Balance b3 = new Balance("4", "3", 30);

        List<Balance> expectedBalances = List.of(b1, b2, b3);
        expectedBalances.forEach(balance -> Assert.assertTrue(balances.contains(balance)));
    }

    @Test
    public void computeBalanceWhenEveryonePaid() {
        List<Share> shares = new ArrayList<>();
        shares.add(new Share("1", 100, 100));
        shares.add(new Share("2", 100, 100));
        shares.add(new Share("3", 100, 100));
        shares.add(new Share("4", 100, 100));

        List<Balance> balances = service.computeBalancesFromSplit(new Split("id", shares));
        Assert.assertNotNull(balances);
        Assert.assertEquals(0, balances.size());
    }
}