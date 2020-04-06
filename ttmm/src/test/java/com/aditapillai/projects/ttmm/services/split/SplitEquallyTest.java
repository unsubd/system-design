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
import java.util.function.Function;
import java.util.stream.Collectors;

class SplitEquallyTest {
    private final SplitService service = new SplitService();

    @Test
    public void splitEquallyWhenAllPaid() {
        Map<String, Double> payments = new HashMap<>();
        payments.put("1", 100.0);
        payments.put("2", 100.0);
        payments.put("3", 100.0);
        payments.put("4", 100.0);

        Split split = this.service.splitEqually(new Bill(400, "1", List.of("1", "2", "3", "4")), payments);
        Assert.assertNotNull(split);
        List<Share> shares = split.getShares();
        Assert.assertEquals(4, shares.size());
        shares.forEach(share -> {
            Assert.assertEquals(0, Double.compare(100.0, share.getToPay()));
            Assert.assertEquals(0, Double.compare(100.0, share.getPaid()));
        });

    }

    @Test
    public void splitEquallyWhenOnePaid() {
        Map<String, Double> payments = new HashMap<>();
        payments.put("1", 400.0);

        Split split = this.service.splitEqually(new Bill(400, "1", List.of("1", "2", "3", "4")), payments);
        List<Share> shares = split.getShares();

        Assert.assertNotNull(split);
        Assert.assertEquals(4, shares.size());

        shares.forEach(share -> Assert.assertEquals(0, Double.compare(100.0, share.getToPay())));

        Share paidShare = shares.stream()
                                .filter(share -> "1".equals(share.getUserId()))
                                .findFirst()
                                .get();

        List<Share> unpaidShares = shares.stream()
                                         .filter(share -> !"1".equals(share.getUserId()))
                                         .collect(Collectors.toList());


        Assert.assertEquals(0, Double.compare(400.0, paidShare.getPaid()));
        unpaidShares.forEach(share -> Assert.assertEquals(0, Double.compare(0.0, share.getPaid())));
    }

    @Test
    public void splitEquallyWhenTwoPaid() {
        Map<String, Double> payments = new HashMap<>();
        payments.put("1", 250.0);
        payments.put("2", 150.0);

        Split split = this.service.splitEqually(new Bill(400, "1", List.of("1", "2", "3", "4")), payments);
        List<Share> shares = split.getShares();

        Assert.assertNotNull(split);
        Assert.assertEquals(4, shares.size());

        shares.forEach(share -> Assert.assertEquals(0, Double.compare(100.0, share.getToPay())));

        Map<String, List<Share>> paidUnpaidMap = shares.stream()
                                                       .collect(Collectors.groupingBy(o -> o.getToPay() > o.getPaid() ?
                                                                       "BORROWERS": o.getPaid() > o.getToPay() ?
                                                                       "LENDERS": "OTHERS",
                                                               Collectors.mapping(Function.identity(), Collectors.toList())));

        Assert.assertEquals(2, paidUnpaidMap.get("LENDERS")
                                            .size());
        Assert.assertEquals(2, paidUnpaidMap.get("BORROWERS")
                                            .size());
    }
}