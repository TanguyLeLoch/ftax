package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerEntry;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

    @Id
    private String id;

    private Date date;

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

    public LedgerEntryEntity(String id, LedgerBookEntity ledgerBook, List<BalanceEntity> balances, Date date) {
        this.id = id;
        this.ledgerBook = ledgerBook;
        this.balances = balances;
        this.date = date;
    }

    public static LedgerEntryEntity fromDomain(LedgerEntry ledgerEntry, String ledgerBookId) {
        return new LedgerEntryEntity(
            ledgerEntry.getId(),
            new LedgerBookEntity(ledgerBookId),
            ledgerEntry.getBalances().values().stream().map(BalanceEntity::fromDomain).collect(Collectors.toList()),
            ledgerEntry.getDate()
        );
    }

    public LedgerEntry toDomain() {
        return new LedgerEntry(
            id,
            balances.stream().collect(Collectors.toMap(BalanceEntity::getToken, BalanceEntity::toDomain)),
            date
        );
    }
}
