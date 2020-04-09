package com.aditapillai.projects.ttmm.dao;

import com.aditapillai.projects.ttmm.models.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceDao {
    public static final String LENDER = "lender";
    public static final String BORROWER = "borrower";
    public static final String AMOUNT = "amount";
    private MongoOperations mongoOperations;

    /**
     * Save all the balances as one bulk operation
     *
     * @param balances to be stored
     * @return true if the balances were stored
     */
    public boolean saveAll(List<Balance> balances) {
        BulkOperations bulkOperations = this.mongoOperations.bulkOps(BulkOperations.BulkMode.UNORDERED, Balance.class);

        balances
                .forEach(balance -> bulkOperations.upsert(
                        new Query(Criteria.where(LENDER)
                                          .is(balance.getLender())
                                          .and(BORROWER)
                                          .is(balance.getBorrower())),
                        new Update().inc(AMOUNT, balance.getAmount())
                        )
                );

        bulkOperations.execute();

        return true;
    }

    /**
     * Fetch all the balances for the given user
     *
     * @param userId of the user
     * @return list of balances
     */
    public List<Balance> findBalancesForId(String userId) {
        Query query = new Query(Criteria.where(LENDER)
                                        .is(userId)
                                        .orOperator(Criteria.where(BORROWER)
                                                            .is(userId)));
        return this.mongoOperations.find(query, Balance.class);
    }

    @Autowired
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
