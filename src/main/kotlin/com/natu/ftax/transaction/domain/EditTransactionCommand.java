package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

public class EditTransactionCommand {
    private final String id;
    private final TransactionType transactionType;
    private final Instant instant;
    private final Value valueIn;
    private final Value valueOut;
    private final Value valueFee;
    private final String externalId;

    public EditTransactionCommand(
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
        this.transactionType = TransactionType.forValue(transactionType);
        this.instant = instant;
        this.valueIn = createValueWithCheck(tokenIn, amountIn, "In");
        this.valueOut = createValueWithCheck(tokenOut, amountOut, "Out");
        this.valueFee = createValueWithCheck(tokenFee, amountFee, "Fee");
        this.externalId = externalId;
    }

    private static @Nullable Value createValueWithCheck(String token,
            BigDecimal amount, String side) {

        checkValue(token, amount, side);
        if (token == null) {
            return null;
        }

        return new Value(new Token(token), amount);
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
            return;
        }
        if (instant != null) {
            transaction.setInstant(instant);
            return;
        }
        if (valueIn != null) {
            transaction.setValueIn(valueIn);
            return;
        }
        if (valueOut != null) {
            transaction.setValueOut(valueOut);
        }
        if (valueFee != null) {
            transaction.setValueFee(valueFee);
        }
        if (externalId != null) {
            transaction.setExternalId(externalId);
        }
    }


}
