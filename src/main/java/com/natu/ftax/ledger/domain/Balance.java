package com.natu.ftax.ledger.domain;

import com.natu.ftax.transaction.domain.OldToken;

import java.math.BigDecimal;

public class Balance {
    private final String id;
    private final BigDecimal amount;
    private final OldToken oldToken;

    public Balance(String id, BigDecimal amount, OldToken oldToken) {
        this.id = id;
        this.amount = amount;
        this.oldToken = oldToken;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OldToken getToken() {
        return oldToken;
    }
}
