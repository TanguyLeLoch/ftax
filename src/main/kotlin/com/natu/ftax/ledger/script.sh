#!/bin/bash

# Create necessary directories
mkdir -p ./domain ./infrastructure

# Write Java content to corresponding files

# Balance.java
cat <<EOL | tee ./domain/Balance.java
package com.natu.ftax.ledger.domain;

import com.natu.ftax.transaction.domain.Token;

public class Balance {
    private final String id;
    private final double amount;
    private final Token token;

    public Balance(String id, double amount, Token token) {
        this.id = id;
        this.amount = amount;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public Token getToken() {
        return token;
    }
}
EOL

# LedgerEntry.java
cat <<EOL | tee ./domain/LedgerEntry.java
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
EOL

# LedgerBook.java
cat <<EOL | tee ./domain/LedgerBook.java
package com.natu.ftax.ledger.domain;

import java.util.ArrayList;
import java.util.List;

public class LedgerBook {
    private final String id;
    private final List<LedgerEntry> ledgerEntries;

    public LedgerBook(String id) {
        this.id = id;
        this.ledgerEntries = new ArrayList<>();
    }

    public LedgerBook(String id, List<LedgerEntry> ledgerEntries) {
        this.id = id;
        this.ledgerEntries = ledgerEntries;
    }

    public static LedgerBook create(String id) {
        return new LedgerBook(id);
    }

    public String getId() {
        return id;
    }

    public List<LedgerEntry> getLedgerEntries() {
        return ledgerEntries;
    }
}
EOL

# BalanceRepositoryJdbc.java
cat <<EOL | tee ./infrastructure/BalanceRepositoryJdbc.java
package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.Balance;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class BalanceRepositoryJdbc implements BalanceRepository {

    private final BalanceRepositoryJpa balanceRepositoryJpa;

    public BalanceRepositoryJdbc(BalanceRepositoryJpa balanceRepositoryJpa) {
        this.balanceRepositoryJpa = balanceRepositoryJpa;
    }

    @Override
    public void save(Collection<Balance> balances) {
        balanceRepositoryJpa.saveAll(balances.stream().map(BalanceEntity::fromDomain).toList());
    }

    @Override
    public void cleanOrphanBalances() {
        balanceRepositoryJpa.cleanOrphanBalances();
    }
}
EOL

# LedgerBookEntity.java
cat <<EOL | tee ./infrastructure/LedgerBookEntity.java
package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerBook;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ledger_books")
public class LedgerBookEntity {

    @Id
    private String id;

    @OneToMany(mappedBy = "ledgerBook", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<LedgerEntryEntity> ledgerEntries;

    protected LedgerBookEntity() {
        // No-arg constructor required by JPA
    }

    public LedgerBookEntity(String id, List<LedgerEntryEntity> ledgerEntries) {
        this.id = id;
        this.ledgerEntries = ledgerEntries;
    }

    public static LedgerBookEntity fromDomain(LedgerBook ledgerBook) {
        return new LedgerBookEntity(
            ledgerBook.getId(),
            ledgerBook.getLedgerEntries().stream().map(entry -> LedgerEntryEntity.fromDomain(entry, ledgerBook.getId())).collect(Collectors.toList())
        );
    }

    public LedgerBook toDomain() {
        return new LedgerBook(
            id,
            ledgerEntries.stream().map(LedgerEntryEntity::toDomain).collect(Collectors.toList())
        );
    }
}
EOL
