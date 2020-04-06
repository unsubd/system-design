package com.aditapillai.projects.ttmm.services.split;

import com.aditapillai.projects.ttmm.models.Bill;
import com.aditapillai.projects.ttmm.models.Share;
import com.aditapillai.projects.ttmm.models.Split;
import com.aditapillai.projects.ttmm.services.SplitService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SplitByAmountsTest {
    private final SplitService service = new SplitService();

    @Test
    public void splitEquallyByPercentagesTest() {
        Map<String, Double> payments = new HashMap<>();
        payments.put("1", 200.0);
        payments.put("2", 100.0);

        Map<String, Double> owedAmounts = new HashMap<>();
        owedAmounts.put("1", 100.0);
        owedAmounts.put("2", 25.0);
        owedAmounts.put("3", 250.0);
        owedAmounts.put("4", 25.0);

        Split split = this.service.splitByAmounts(new Bill(400.0, "1", List.of("1", "2", "3", "4")), payments, owedAmounts);

        List<Share> shares = split.getShares();

        Assert.assertNotNull(shares);
        Assert.assertEquals(4, shares.size());

        Map<String, List<Share>> collect = shares.stream()
                                                 .collect(Collectors.groupingBy(Share::getUserId));

        Assert.assertEquals(0, Double.compare(200, collect.get("1")
                                                          .get(0)
                                                          .getPaid()));

        Assert.assertEquals(0, Double.compare(100, collect.get("1")
                                                          .get(0)
                                                          .getToPay()));

        Assert.assertEquals(0, Double.compare(100, collect.get("2")
                                                          .get(0)
                                                          .getPaid()));

        Assert.assertEquals(0, Double.compare(25, collect.get("2")
                                                         .get(0)
                                                         .getToPay()));

        Assert.assertEquals(0, Double.compare(0, collect.get("3")
                                                        .get(0)
                                                        .getPaid()));
        Assert.assertEquals(0, Double.compare(250, collect.get("3")
                                                          .get(0)
                                                          .getToPay()));

        Assert.assertEquals(0, Double.compare(0, collect.get("4")
                                                        .get(0)
                                                        .getPaid()));

        Assert.assertEquals(0, Double.compare(25, collect.get("4")
                                                         .get(0)
                                                         .getToPay()));


    }
}