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

    override fun get(): LedgerBook? {
        val result =  ledgerBookRepositoryJpa.findAll().map { it.toDomain() }.toMutableList()
        return if (result.isEmpty()) null else result[0]
    }

    @Transactional
    override fun delete(ledgerBookId: String) {
        ledgerBookRepositoryJpa.deleteById(ledgerBookId)
        balanceRepository.cleanOrphanBalances()
    }
}