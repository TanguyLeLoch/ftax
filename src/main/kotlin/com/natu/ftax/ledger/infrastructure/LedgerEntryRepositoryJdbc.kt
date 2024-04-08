package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.LedgerEntry
import org.springframework.stereotype.Repository

@Repository
class LedgerEntryRepositoryJdbc(val ledgerEntryRepositoryJpa: LedgerEntryRepositoryJpa) : LedgerEntryRepository {
    override fun save(ledgerEntry: LedgerEntry, ledgerBookId: String) {
        ledgerEntryRepositoryJpa.save(LedgerEntryEntity.fromDomain(ledgerEntry, ledgerBookId))
    }
}