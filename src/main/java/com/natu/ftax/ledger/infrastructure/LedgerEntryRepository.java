package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.LedgerEntry;

public interface LedgerEntryRepository {
    void save(LedgerEntry ledgerEntry, String ledgerBookId);
}
