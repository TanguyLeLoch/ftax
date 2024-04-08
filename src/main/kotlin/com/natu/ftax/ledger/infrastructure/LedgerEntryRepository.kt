package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.LedgerEntry

interface LedgerEntryRepository {
    fun save(ledgerEntry: LedgerEntry, ledgerBookId: String)
}