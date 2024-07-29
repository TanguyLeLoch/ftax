package com.natu.ftax.transaction.presentation;

import com.natu.ftax.transaction.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {

    private final Transaction transaction;

    public TransactionResponse(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getId(){
        return transaction.getId();
    }

    public Instant getDateTime() {
        return transaction.getInstant();
    }

    public String getState() {
        return transaction.getState();
    }

    public String getType() {
        return transaction.getTransactionType();
    }

    public String getTokenIn(){
        return transaction.getSymbolIn();
    }

    public String getTokenOut() {
        return transaction.getSymbolOut();
    }

    public String getTokenFee() {
        return transaction.getSymbolFee();
    }

    public BigDecimal getAmountIn() {
        return transaction.getAmountIn();
    }

    public BigDecimal getAmountOut() {
        return transaction.getAmountOut();
    }

    public BigDecimal getAmountFee() {
        return transaction.getAmountFee();
    }

    public String getExternalId(){
        return transaction.getExternalId();
    }

}
