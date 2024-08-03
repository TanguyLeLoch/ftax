package com.natu.ftax.transaction.presentation;

import com.natu.ftax.transaction.domain.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(name = "Transaction", description = "Transaction object")
public class TransactionResponse {


    private final Transaction transaction;

    public TransactionResponse(Transaction transaction) {
        this.transaction = transaction;
    }

    @NotNull
    public String getId() {
        return transaction.getId();
    }

    public Instant getDateTime() {
        return transaction.getInstant();
    }

    @NotNull
    @Schema(description = "The state of the transaction", allowableValues = {"draft", "submitted"})
    public String getState() {
        return transaction.getState();
    }

    @NotNull
    @Schema(description = "The type of the transaction", allowableValues = {"transfer", "swap", "none"})
    public String getTransactionType() {
        return transaction.getTransactionType();
    }

    public String getTokenIn() {
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

    public String getExternalId() {
        return transaction.getExternalId();
    }

}
