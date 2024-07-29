package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;


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
        state = TransactionState.DRAFT;
        transactionType = TransactionType.NONE;
        valueIn = Value.dummyValue();
        valueOut = Value.dummyValue();
        valueFee = Value.dummyValue();
        instant = Instant.now();

    }

    public static Transaction reconstitute(String id, String state,
            String transactionType, Instant instant, Value valueIn,
            Value valueOut, Value valueFee, String externalId) {
        Transaction tx = new Transaction(id);
        tx.state = TransactionState.from(state);
        tx.transactionType = TransactionType.from(transactionType);
        tx.instant = instant;
        tx.valueIn = valueIn;
        tx.valueOut = valueOut;
        tx.valueFee = valueFee;
        tx.externalId = externalId;
        return tx;
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
        this.state = TransactionState.SUBMITTED;
    }

    public void edit() {
        if (state != TransactionState.SUBMITTED) {
            throw new FunctionalException(
                    "Transaction is not in SUBMITTED state");
        }
        this.state = TransactionState.DRAFT;
    }

    private void checkIsDraft() {
        if (state != TransactionState.DRAFT) {
            throw new FunctionalException("Transaction is not in DRAFT state");
        }
    }

    public String getState() {
        return state.getValue();
    }

    public String getTransactionType() {
        return transactionType.getValue();
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

    public BigDecimal getAmountIn() {
        return valueIn.getAmount();
    }

    public BigDecimal getAmountOut() {
        return valueOut.getAmount();
    }

    public BigDecimal getAmountFee() {
        return valueFee.getAmount();
    }

    public String getSymbolIn() {
        return valueIn.getSymbol();
    }

    public String getSymbolOut() {
        return valueOut.getSymbol();

    }

    public String getSymbolFee() {
        return valueOut.getSymbol();

    }

}
