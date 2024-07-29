package com.natu.ftax.ledger.domain;

import com.natu.ftax.transaction.domain.Token;

import java.math.BigDecimal;

public class Balance {
    private final String id;
    private final BigDecimal amount;
    private final Token token;

    public Balance(String id, BigDecimal amount, Token token) {
        this.id = id;
        this.amount = amount;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Token getToken() {
        return token;
    }
}
