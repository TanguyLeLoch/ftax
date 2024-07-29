package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerEntry;
import org.springframework.stereotype.Repository;

@Repository
public class LedgerEntryRepositoryJdbc implements LedgerEntryRepository {

    private final LedgerEntryRepositoryJpa ledgerEntryRepositoryJpa;

    public LedgerEntryRepositoryJdbc(LedgerEntryRepositoryJpa ledgerEntryRepositoryJpa) {
        this.ledgerEntryRepositoryJpa = ledgerEntryRepositoryJpa;
    }

    @Override
    public void save(LedgerEntry ledgerEntry, String ledgerBookId) {
        ledgerEntryRepositoryJpa.save(LedgerEntryEntity.fromDomain(ledgerEntry, ledgerBookId));
    }
}
