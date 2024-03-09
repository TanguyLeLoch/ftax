package com.natu.ftax.transaction.infrastructure

import com.natu.ftax.common.exception.NotFoundException
import com.natu.ftax.transaction.application.TransactionRepository
import com.natu.ftax.transaction.domain.DraftTransaction
import com.natu.ftax.transaction.domain.Transaction
import org.springframework.stereotype.Repository


@Repository
class TransactionRepositoryInMemory : TransactionRepository {

    private val transactions = hashMapOf<String, Transaction>()
    private val draftTransactions = hashMapOf<String, DraftTransaction>()


    override fun getTransactionById(id: String): Transaction {
        return transactions[id] ?: throw NotFoundException("Transaction not found with id: $id")
    }

    override fun getDraftTransactionById(id: String): DraftTransaction {
        return draftTransactions[id] ?: throw NotFoundException("Draft transaction not found with id: $id")
    }

    override fun getAllTransactions(): Array<Transaction> {
        return transactions.values.toTypedArray()
    }

    override fun save(transaction: Transaction) {
        transactions[transaction.id] = transaction
    }

    override fun save(transaction: DraftTransaction) {
        draftTransactions[transaction.id] = transaction
    }
}