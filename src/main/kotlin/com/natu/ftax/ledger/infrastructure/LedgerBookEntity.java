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
