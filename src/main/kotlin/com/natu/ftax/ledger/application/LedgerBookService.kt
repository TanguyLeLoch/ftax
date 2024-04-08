package com.natu.ftax.ledger.application

import LedgerBookRepository
import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.ledger.domain.LedgerBook
import com.natu.ftax.ledger.domain.LedgerEntry
import com.natu.ftax.transaction.application.TransactionService
import org.springframework.stereotype.Service

@Service
class LedgerBookService (val idGenerator: IdGenerator, val ledgerBookRepository: LedgerBookRepository, val transactionService: TransactionService){

    fun generateLedgerBook() {

        val ledgerBook = LedgerBook.create(idGenerator.generate())
        val txs = transactionService.getAllTransactions()
        ledgerBook.ledgerEntries.add(LedgerEntry.first(idGenerator.generate()))
        for (tx in txs) {
            ledgerBook.ledgerEntries.add(LedgerEntry.create(idGenerator.generate(), ledgerBook.ledgerEntries.last(), tx, idGenerator))
        }
        ledgerBookRepository.save(ledgerBook)

    }

    fun getAllLedgerBook(): MutableList<LedgerBook> {
        return ledgerBookRepository.get()

    }

    fun deleteLedgerBook(ledgerBookId: String) {
        ledgerBookRepository.delete(ledgerBookId)
    }
}