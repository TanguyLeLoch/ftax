package com.natu.ftax.ledger.infrastructure

import LedgerBookRepository
import com.natu.ftax.ledger.domain.LedgerBook
import org.springframework.stereotype.Repository


@Repository
class LedgerBookRepositoryImpl(val ledgerBookRepositoryJpa: LedgerBookRepositoryJpa) : LedgerBookRepository {
    override fun save(ledgerBook: LedgerBook) {
        ledgerBookRepositoryJpa.save(LedgerBookEntity.fromDomain(ledgerBook))
    }

    override fun get(): MutableList<LedgerBook> {
       return ledgerBookRepositoryJpa.findAll().map { it.toDomain() }.toMutableList()
    }
}