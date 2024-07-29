package com.natu.ftax.transaction;

import com.natu.ftax.transaction.domain.SubmitTransactionCommand;
import com.natu.ftax.transaction.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionTestUtils {

    public static Transaction draftTransaction(String id) {
        return Transaction.create(id);
    }

    public static Transaction submittedTransaction(String id) {
        Transaction tx = draftTransaction(id);
        tx.submit(new SubmitTransactionCommand(
                id,
                "transfer",
                Instant.now(),
                "BTC",
                "ETH",
                "BTC",
                BigDecimal.valueOf(1.0),
                BigDecimal.valueOf(2.0),
                BigDecimal.valueOf(0.01),
                "ext-123"
        ));
        return tx;
    }

    public static Transaction submittedTransactionWithValue(String id,
            double amountIn,
            double amountOut,
            double amountFee,
            String tokenIn,
            String tokenOut,
            String tokenFee) {
        Transaction tx = draftTransaction(id);
        tx.submit(new SubmitTransactionCommand(
                id,
                "transfer",
                Instant.now(),
                tokenIn,
                tokenOut,
                tokenFee,
                BigDecimal.valueOf(amountIn),
                BigDecimal.valueOf(amountOut),
                BigDecimal.valueOf(amountFee),
                "ext-123"
        ));
        return tx;
    }
}