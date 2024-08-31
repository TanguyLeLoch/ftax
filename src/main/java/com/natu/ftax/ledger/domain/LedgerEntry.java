package com.natu.ftax.ledger.domain;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.domain.OldToken;
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
    private final Map<OldToken, Balance> balances;
    @Getter
    private final Instant instant;

    public LedgerEntry(String id, Map<OldToken, Balance> balances,
            Instant instant) {
        this.id = id;
        this.balances = balances;
        this.instant = instant;
    }

    public static LedgerEntry create(String id, LedgerEntry previousEntry, Transaction tx, IdGenerator idGenerator) {
        Map<OldToken, Balance> balances = new HashMap<>(
                previousEntry.getBalances());
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
        OldToken oldTokenIn = transaction.getTokenIn();
        BigDecimal oldBalanceTokenIn = balances.getOrDefault(
                        oldTokenIn, new Balance(balanceId, BigDecimal.ZERO, oldTokenIn))
                .getAmount();
        BigDecimal newBalanceTokenIn = oldBalanceTokenIn.add(transaction.getAmountIn());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenIn) == 0) {
            balances.remove(oldTokenIn);
            return;
        }
        balances.put(oldTokenIn, new Balance(balanceId, newBalanceTokenIn,
                oldTokenIn));
    }

    private void adjustBalanceOut(Transaction transaction, String balanceId) {
        if (BigDecimal.ZERO.compareTo(transaction.getAmountOut()) == 0) return;
        OldToken oldTokenOut = transaction.getTokenOut();
        BigDecimal oldBalanceTokenOut = balances.getOrDefault(
                oldTokenOut, new Balance(balanceId, BigDecimal.ZERO,
                        oldTokenOut)).getAmount();
        BigDecimal newBalanceTokenOut = oldBalanceTokenOut.subtract(transaction.getAmountOut());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenOut) == 0) {
            balances.remove(oldTokenOut);
            return;
        }
        balances.put(oldTokenOut, new Balance(balanceId, newBalanceTokenOut,
                oldTokenOut));
    }

    private void adjustBalanceFee(Transaction transaction, String balanceId) {
        if (BigDecimal.ZERO.compareTo(transaction.getAmountFee()) == 0) return;
        OldToken oldTokenFee = transaction.getTokenFee();
        BigDecimal oldBalanceTokenFee = balances.getOrDefault(
                oldTokenFee, new Balance(balanceId, BigDecimal.ZERO,
                        oldTokenFee)).getAmount();
        BigDecimal newBalanceTokenFee = oldBalanceTokenFee.add(transaction.getAmountFee());
        if (BigDecimal.ZERO.compareTo(newBalanceTokenFee) == 0) {
            balances.remove(oldTokenFee);
            return;
        }
        balances.put(oldTokenFee, new Balance(balanceId, newBalanceTokenFee,
                oldTokenFee));
    }

    public Map<OldToken, Balance> getBalances() {
        return Collections.unmodifiableMap(balances);
    }

}