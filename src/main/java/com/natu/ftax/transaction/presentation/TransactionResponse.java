package com.natu.ftax.transaction.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.natu.ftax.transaction.domain.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    public ZonedDateTime getDateTime() {
        return transaction.getInstant().atZone(ZoneId.of("UTC"));
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
