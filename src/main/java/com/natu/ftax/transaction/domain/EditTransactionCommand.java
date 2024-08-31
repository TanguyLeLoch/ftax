package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;

import java.math.BigDecimal;
import java.time.Instant;

public class EditTransactionCommand {
    private final String id;
    private final TransactionType transactionType;
    private final Instant instant;
    private final Value valueIn;
    private final Value valueOut;
    private final Value valueFee;
    private final Value valueFiat;
    private final String externalId;

    public EditTransactionCommand(
            String id,
            String transactionType,
            Instant instant,
            String tokenIn,
            String tokenOut,
            String tokenFee,
            String tokenFiat,
            BigDecimal amountIn,
            BigDecimal amountOut,
            BigDecimal amountFee,
            BigDecimal amountFiat,
            String externalId
    ) {
        this.id = id;
        this.transactionType = transactionType == null ? null
                : TransactionType.from(transactionType);
        this.instant = instant;
        this.valueIn = createValueWithCheck(tokenIn, amountIn, "In");
        this.valueOut = createValueWithCheck(tokenOut, amountOut, "Out");
        this.valueFee = createValueWithCheck(tokenFee, amountFee, "Fee");
        this.valueFiat = createValueWithCheck(tokenFiat, amountFiat, "Fiat");
        this.externalId = externalId;
    }

    private static Value createValueWithCheck(String token,
            BigDecimal amount, String side) {

        checkValue(token, amount, side);
        if (token == null) {
            return null;
        }

        return new Value(new OldToken(token), amount);
    }

    private static void checkValue(String tokenIn, BigDecimal amountIn,
            String side) {
        if (tokenIn == null ^ amountIn ==null) {
            throw new FunctionalException("field token{0} and amount{0} have to sent together", side);
        }
    }

    public void execute(Transaction transaction) {
        if (transactionType != null) {
            transaction.setTransactionType(transactionType);
        }
        if (instant != null) {
            transaction.setInstant(instant);
        }
        if (valueIn != null) {
            transaction.setValueIn(valueIn);
        }
        if (valueOut != null) {
            transaction.setValueOut(valueOut);
        }
        if (valueFee != null) {
            transaction.setValueFee(valueFee);
        }
        if (valueFiat != null) {
            transaction.setValueFiat(valueFiat);
        }
        if (externalId != null) {
            transaction.setExternalId(externalId);
        }
    }


}
