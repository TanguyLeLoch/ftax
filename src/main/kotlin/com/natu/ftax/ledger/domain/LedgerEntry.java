package com.natu.ftax.ledger.domain;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.domain.Token;
import com.natu.ftax.transaction.domain.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LedgerEntry {
    private final String id;
    private final Map<Token, Balance> balances;
    private final Date date;

    public LedgerEntry(String id, Map<Token, Balance> balances, Date date) {
        this.id = id;
        this.balances = balances;
        this.date = date;
    }

    public static LedgerEntry create(String id, LedgerEntry previousEntry, Transaction tx, IdGenerator idGenerator) {
        Map<Token, Balance> balances = new HashMap<>(previousEntry.getBalances());
        LedgerEntry ledgerEntry = new LedgerEntry(id, balances, tx.getDate());
        ledgerEntry.adjustBalanceIn(tx, idGenerator.generate());
        ledgerEntry.adjustBalanceOut(tx, idGenerator.generate());
        ledgerEntry.adjustBalanceFee(tx, idGenerator.generate());
        return ledgerEntry;
    }

    public static LedgerEntry first(String id) {
        return new LedgerEntry(id, new HashMap<>(), new Date());
    }

    private void adjustBalanceIn(Transaction transaction, String balanceId) {
        if (transaction.getAmountIn() == 0.0) return;
        Token tokenIn = transaction.getTokenIn();
        double oldBalanceTokenIn = balances.getOrDefault(tokenIn, new Balance(balanceId, 0.0, tokenIn)).getAmount();
        double newBalanceTokenIn = oldBalanceTokenIn + transaction.getAmountIn();
        if (newBalanceTokenIn == 0.0) {
            balances.remove(tokenIn);
            return;
        }
        balances.put(tokenIn, new Balance(balanceId, newBalanceTokenIn, tokenIn));
    }

    private void adjustBalanceOut(Transaction transaction, String balanceId) {
        if (transaction.getAmountOut() == 0.0) return;
        Token tokenOut = transaction.getTokenOut();
        double oldBalanceTokenOut = balances.getOrDefault(tokenOut, new Balance(balanceId, 0.0, tokenOut)).getAmount();
        double newBalanceTokenOut = oldBalanceTokenOut - transaction.getAmountOut();
        if (newBalanceTokenOut == 0.0) {
            balances.remove(tokenOut);
            return;
        }
        balances.put(tokenOut, new Balance(balanceId, newBalanceTokenOut, tokenOut));
    }

    private void adjustBalanceFee(Transaction transaction, String balanceId) {
        if (transaction.getAmountFee() == 0.0) return;
        Token tokenFee = transaction.getTokenFee();
        double oldBalanceTokenFee = balances.getOrDefault(tokenFee, new Balance(balanceId, 0.0, tokenFee)).getAmount();
        double newBalanceTokenFee = oldBalanceTokenFee + transaction.getAmountFee();
        if (newBalanceTokenFee == 0.0) {
            balances.remove(tokenFee);
            return;
        }
        balances.put(tokenFee, new Balance(balanceId, newBalanceTokenFee, tokenFee));
    }

    public String getId() {
        return id;
    }

    public Map<Token, Balance> getBalances() {
        return balances;
    }

    public Date getDate() {
        return date;
    }
}
