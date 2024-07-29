package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;


public class Transaction {

    @Getter
    private final String id;
    private TransactionState state;
    private TransactionType transactionType;
    @Getter
    private Instant instant;
    private Value valueIn;
    private Value valueOut;
    private Value valueFee;
    @Getter
    private String externalId;

    private Transaction(String id) {
        this.id = id;
        this.state = TransactionState.DRAFT;
        this.transactionType = TransactionType.NONE;
    }

    public static Transaction reconstitute(String id, String state,
            String transactionType, Instant instant, Value valueIn,
            Value valueOut, Value valueFee, String externalId) {
        Transaction tx = new Transaction(id);
        tx.state = TransactionState.valueOf(state);
        tx.transactionType = TransactionType.valueOf(transactionType);
        tx.instant = instant;
        tx.valueIn = valueIn;
        tx.valueOut = valueOut;
        tx.valueFee = valueFee;
        tx.externalId = externalId;
        return tx;
    }

    private void validate() {
    }

    public static Transaction create(String id) {
        return new Transaction(id);
    }

    public void submit(SubmitTransactionCommand command) {
        checkIsDraft();
        this.transactionType = command.getTransactionType();
        this.instant = command.getInstant();
        this.valueIn = command.getValueIn();
        this.valueOut = command.getValueOut();
        this.valueFee = command.getValueFee();
        this.externalId = command.getExternalId();
        checkNoNullValues();
        this.state = TransactionState.SUBMITTED;
    }

    private void checkNoNullValues() {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("ID cannot be blank");
        if (transactionType == TransactionType.NONE)
            throw new IllegalArgumentException("Transaction type cannot be NONE");
        if (instant == null)
            throw new IllegalArgumentException("Date cannot be null");
        if (valueIn == null) {
            throw new IllegalArgumentException("Value In cannot be null");
        }
        if (valueOut == null) {
            throw new IllegalArgumentException("Value Out cannot be null");
        }
        if (valueFee == null) {
            throw new IllegalArgumentException("Value Fee cannot be null");
        }
    }

    public void edit() {
        if (state != TransactionState.SUBMITTED) {
            throw new FunctionalException("Transaction is not in SUBMITTED state");
        }
        this.state = TransactionState.DRAFT;
    }

    private void checkIsDraft() {
        if (state != TransactionState.DRAFT) {
            throw new FunctionalException("Transaction is not in DRAFT state");
        }
    }

    public String getState() {
        return state.name();
    }

    public String getTransactionType() {
        return transactionType.name();
    }



    public Token getTokenIn() {
        return valueIn.getToken();
    }

    public Token getTokenOut() {
        return valueOut.getToken();
    }

    public Token getTokenFee() {
        return valueFee.getToken();
    }

    public BigDecimal getAmountIn() {
        return valueIn.getAmount();
    }

    public BigDecimal getAmountOut() {
        return valueOut.getAmount();
    }

    public BigDecimal getAmountFee() {
        return valueFee.getAmount();
    }

    void setTransactionType(TransactionType transactionType) {
        checkIsDraft();
        this.transactionType = transactionType;
    }

    void setInstant(Instant instant) {
        checkIsDraft();
        this.instant = instant;
    }

    void setValueIn(Value valueIn) {
        checkIsDraft();
        this.valueIn = valueIn;
    }

    void setValueOut(Value valueOut) {
        checkIsDraft();
        this.valueOut = valueOut;
    }
    void setValueFee(Value valueFee) {
        checkIsDraft();
        this.valueFee = valueFee;
    }

    void setExternalId(String externalId) {
        checkIsDraft();
        this.externalId = externalId;
    }
}
