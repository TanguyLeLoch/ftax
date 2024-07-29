package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerEntry;
import com.natu.ftax.transaction.domain.Token;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

    @Id
    private String id;

    private Instant instant;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ledger_book_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private LedgerBookEntity ledgerBook;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "ledger_entry_balance",
        joinColumns = @JoinColumn(name = "ledger_entry_id"),
        inverseJoinColumns = @JoinColumn(name = "balance_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<BalanceEntity> balances;

    protected LedgerEntryEntity() {
        // No-arg constructor required by JPA
    }

    public LedgerEntryEntity(String id, LedgerBookEntity ledgerBook,
            List<BalanceEntity> balances, Instant instant) {
        this.id = id;
        this.ledgerBook = ledgerBook;
        this.balances = balances;
        this.instant = instant;
    }

    public static LedgerEntryEntity fromDomain(LedgerEntry ledgerEntry, String ledgerBookId) {
        return new LedgerEntryEntity(
            ledgerEntry.getId(),
            new LedgerBookEntity(ledgerBookId),
            ledgerEntry.getBalances().values().stream().map(BalanceEntity::fromDomain).collect(Collectors.toList()),
                ledgerEntry.getInstant()
        );
    }

    public LedgerEntry toDomain() {
        return new LedgerEntry(
            id,
                balances.stream().collect(Collectors.toMap(e -> new Token(e.getToken()), BalanceEntity::toDomain)),
                instant
        );
    }
}
