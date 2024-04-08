package com.natu.ftax.ledger.infrastructure

import LedgerBookRepository
import com.natu.ftax.ledger.domain.LedgerBook
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository


@Repository
class LedgerBookRepositoryJdbc(
    val ledgerBookRepositoryJpa: LedgerBookRepositoryJpa,
    val ledgerEntryRepository: LedgerEntryRepository,
    val balanceRepository : BalanceRepository
) : LedgerBookRepository {
    @Transactional
    override fun save(ledgerBook: LedgerBook) {
        ledgerBook.ledgerEntries.forEach {
            ledgerEntryRepository.save(it, ledgerBook.id)
        }

        balanceRepository.cleanOrphanBalances()
    }

    override fun get(): MutableList<LedgerBook> {
        return ledgerBookRepositoryJpa.findAll().map { it.toDomain() }.toMutableList()
    }

    @Transactional
    override fun delete(ledgerBookId: String) {
        ledgerBookRepositoryJpa.deleteById(ledgerBookId)
        balanceRepository.cleanOrphanBalances()
    }
}