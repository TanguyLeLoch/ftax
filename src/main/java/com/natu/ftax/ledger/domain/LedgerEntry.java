package com.natu.ftax.ledger.domain;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.domain.Token;
import com.natu.ftax.transaction.domain.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LedgerEntry {
    @Getter
    private final String id;
    @NotNull
    private final Map<Token, Balance> balances;
    @Getter
    private final Instant instant;

    public LedgerEntry(String id, Map<Token, Balance> balances,
            Instant instant) {
        this.id = id;
        this.balances = balances;
        this.instant = instant;
    }

    public static LedgerEntry create(String id, LedgerEntry previousEntry, Transaction tx, IdGenerator idGenerator) {
        Map<Token, Balance> balances = new HashMap<>(previousEntry.getBalances());
        LedgerEntry ledgerEntry = new LedgerEntry(id, balances, tx.getInstant());
        ledgerEntry.adjustBalanceIn(tx, idGenerator.generate());
        ledgerEntry.adjustBalanceOut(tx, idGenerator.generate());
        ledgerEntry.adjustBalanceFee(tx, idGenerator.generate());
        return ledgerEntry;
    }

    public static LedgerEntry first(String id) {
        return new LedgerEntry(id, new HashMap<>(), Instant.now());
    }

    private void adjustBalanceIn(Transaction transaction, String balanceId) {
        if (BigDecimal.ZERO.compareTo(transaction.getAmountIn()) == 0) return;
        Token tokenIn = transaction.getTokenIn();
        BigDecimal oldBalanceTokenIn = balances.getOrDefault(tokenIn, new  Balance(balanceId, BigDecimal.ZERO, tokenIn)).getAmount();
        BigDecimal newBalanceTokenIn = oldBalanceTokenIn.add(transaction.getAmountIn());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenIn) == 0) {
            balances.remove(tokenIn);
            return;
        }
        balances.put(tokenIn, new Balance(balanceId, newBalanceTokenIn, tokenIn));
    }

    private void adjustBalanceOut(Transaction transaction, String balanceId) {
        if (BigDecimal.ZERO.compareTo(transaction.getAmountOut()) == 0) return;
        Token tokenOut = transaction.getTokenOut();
        BigDecimal oldBalanceTokenOut = balances.getOrDefault(tokenOut, new Balance(balanceId, BigDecimal.ZERO, tokenOut)).getAmount();
        BigDecimal newBalanceTokenOut = oldBalanceTokenOut.subtract(transaction.getAmountOut());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenOut) == 0) {
            balances.remove(tokenOut);
            return;
        }
        balances.put(tokenOut, new Balance(balanceId, newBalanceTokenOut, tokenOut));
    }

    private void adjustBalanceFee(Transaction transaction, String balanceId) {
        if (BigDecimal.ZERO.compareTo(transaction.getAmountFee()) == 0) return;
        Token tokenFee = transaction.getTokenFee();
        BigDecimal oldBalanceTokenFee = balances.getOrDefault(tokenFee, new Balance(balanceId, BigDecimal.ZERO, tokenFee)).getAmount();
        BigDecimal newBalanceTokenFee = oldBalanceTokenFee.add(transaction.getAmountFee());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenFee) == 0) {
            balances.remove(tokenFee);
            return;
        }
        balances.put(tokenFee, new Balance(balanceId, newBalanceTokenFee, tokenFee));
    }

    public Map<Token, Balance> getBalances() {
        return Collections.unmodifiableMap(balances);
    }

}