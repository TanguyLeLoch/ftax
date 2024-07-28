package com.natu.ftax.ledger.domain;

import com.natu.ftax.transaction.domain.Token;

public class Balance {
    private final String id;
    private final double amount;
    private final Token token;

    public Balance(String id, double amount, Token token) {
        this.id = id;
        this.amount = amount;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Token getToken() {
        return token;
    }
}
