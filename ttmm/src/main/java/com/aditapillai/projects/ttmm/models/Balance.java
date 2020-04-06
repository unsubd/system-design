package com.aditapillai.projects.ttmm.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Document(collection = "balances")
@AllArgsConstructor
@Getter
public class Balance {
    @NotNull
    private final String lender;
    @NotNull
    private final String borrower;
    private final double amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Double.compare(balance.amount, amount) == 0 &&
                lender.equals(balance.lender) &&
                borrower.equals(balance.borrower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lender, borrower, amount);
    }
}
