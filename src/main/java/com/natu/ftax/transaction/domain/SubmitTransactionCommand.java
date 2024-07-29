package com.natu.ftax.transaction.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class SubmitTransactionCommand {
    private final String id;
    private final TransactionType transactionType;
    private final Instant instant;
    private final Value valueIn;
    private final Value valueOut;
    private final Value valueFee;
    private final String externalId;

    public SubmitTransactionCommand(
            String id,
            String transactionType,
            Instant instant,
            String tokenIn,
            String tokenOut,
            String tokenFee,
            BigDecimal amountIn,
            BigDecimal amountOut,
            BigDecimal amountFee,
            String externalId
    ) {
        this.id = id;
        this.transactionType = TransactionType.from(transactionType);
        this.instant = instant;
        this.valueIn = new Value(new Token(tokenIn), amountIn);
        this.valueOut = new Value(new Token(tokenOut), amountOut);
        this.valueFee = new Value(new Token(tokenFee), amountFee);
        this.externalId = externalId;
    }
}
