package com.aditapillai.projects.ttmm.services;

import com.aditapillai.projects.ttmm.dao.BalanceDao;
import com.aditapillai.projects.ttmm.models.Balance;
import com.aditapillai.projects.ttmm.models.Share;
import com.aditapillai.projects.ttmm.models.Split;
import com.aditapillai.projects.ttmm.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BalanceService {
    public static final String LENDERS = "lenders";
    public static final String BORROWERS = "borrowers";
    private BalanceDao dao;

    /**
     * Given a list of balances, store them.
     *
     * @param balances to be stored
     * @return true if the balances were successfully stored;
     */
    public boolean storeBalances(List<Balance> balances) {
        return this.dao.upsertBalances(balances);
    }

    /**
     * Given a split, compute the balances for all the involved parties
     *
     * @param split of the bill
     * @return balances of all the involved parties
     */
    public List<Balance> computeBalancesFromSplit(Split split) {
        List<Share> shares = split.getShares();
        Map<String, List<Share>> collect = shares.stream()
                                                 .collect(Collectors.groupingBy(o -> o.getToPay() > o.getPaid() ?
                                                                 BORROWERS: o.getPaid() > o.getToPay() ? LENDERS: "others",
                                                         Collectors.mapping(Function.identity(), Collectors.toList())));

        List<Share> borrowerShares = collect.getOrDefault(BORROWERS, new LinkedList<>());
        List<Share> lenderShares = collect.getOrDefault(LENDERS, new LinkedList<>());
        List<Balance> balances = new LinkedList<>();

        for (Share borrowerShare : borrowerShares) {
            double amountBorrowedByTheBorrower = borrowerShare.getToPay() - borrowerShare.getPaid();
            while (amountBorrowedByTheBorrower > 0) {
                Share lenderShare = lenderShares.get(0);
                double amountOwedToTheLender = lenderShare.getPaid() - lenderShare.getToPay();
                double balanceAmount = amountBorrowedByTheBorrower;
                if (balanceAmount >= amountOwedToTheLender) {
                    balanceAmount = amountOwedToTheLender;
                    lenderShares.remove(0);
                }
                amountBorrowedByTheBorrower -= balanceAmount;
                balances.add(new Balance(lenderShare.getUserId(), borrowerShare.getUserId(), balanceAmount));
            }
        }

        return balances;
    }

    /**
     * Settle up the balance between 2 users
     *
     * @param user1 user 1
     * @param user2 user 2
     * @return true if settle up was a success.
     */
    public boolean settleUp(User user1, User user2) {
        Balance b1 = new Balance(user1.getId(), user2.getId(), 0);
        Balance b2 = new Balance(user2.getId(), user1.getId(), 0);
        return this.dao.updateBalances(List.of(b1, b2));
    }

    /**
     * Fetch all the balances for a user
     *
     * @param user whose balances need to be fetched
     * @return list of balances
     */
    public List<Balance> fetchBalancesFor(User user) {
        return this.dao.findBalancesForId(user.getId());
    }

    @Autowired
    public void setDao(BalanceDao dao) {
        this.dao = dao;
    }
}
