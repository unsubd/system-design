package com.aditapillai.projects.ttmm.services;

import com.aditapillai.projects.ttmm.dao.BillDao;
import com.aditapillai.projects.ttmm.dao.SplitDao;
import com.aditapillai.projects.ttmm.models.Bill;
import com.aditapillai.projects.ttmm.models.Share;
import com.aditapillai.projects.ttmm.models.Split;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SplitService {
    private BillDao billDao;
    private SplitDao splitDao;

    /**
     * Split the given bill equally among the involved parties
     *
     * @param bill     to be split
     * @param payments made by the individuals involved
     * @return split of the bill
     */
    public Split splitEqually(Bill bill, Map<String, Double> payments) {
        double percentageShare = 100.0 / bill.getUsers()
                                             .size();
        return this.splitByPercentages(bill, payments, bill.getUsers()
                                                           .stream()
                                                           .collect(Collectors.toMap(Function.identity(),
                                                                   userId -> percentageShare)));
    }

    /**
     * Split the given bill with percentage share of individuals
     *
     * @param bill        to be split
     * @param payments    made by each individual
     * @param percentages percentage share of the total amount for each individual
     * @return split of the bill
     */
    public Split splitByPercentages(Bill bill, Map<String, Double> payments, Map<String, Double> percentages) {
        List<Share> shares = bill.getUsers()
                                 .stream()
                                 .map(userId -> new Share(userId, payments.getOrDefault(userId, 0.0),
                                         bill.getAmount() * percentages.get(userId) / 100))
                                 .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

        return new Split(bill.getId(), shares);
    }

    /**
     * Split the bill based on the owed amounts
     *
     * @param bill        to split
     * @param payments    made by individuals
     * @param owedAmounts amount owed by each individual
     * @return split of the bill
     */
    public Split splitByAmounts(Bill bill, Map<String, Double> payments, Map<String, Double> owedAmounts) {
        List<Share> shares = bill.getUsers()
                                 .stream()
                                 .map(userId -> new Share(userId, payments.getOrDefault(userId, 0.0), owedAmounts.get(userId)))
                                 .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
        return new Split(bill.getId(), shares);
    }

    /**
     * Save the given bill and split to the db
     *
     * @param bill  to be stored
     * @param split to be stored
     * @return true if both bill and split are stored successfully
     */
    public boolean saveBillAndSplit(Bill bill, Split split) {
        this.billDao.save(bill);
        this.splitDao.save(split);
        return true;
    }

    @Autowired
    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    @Autowired
    public void setSplitDao(SplitDao splitDao) {
        this.splitDao = splitDao;
    }

}
