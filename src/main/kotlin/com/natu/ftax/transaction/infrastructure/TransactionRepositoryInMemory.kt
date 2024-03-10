package com.natu.ftax.transaction.infrastructure

import com.natu.ftax.common.exception.NotFoundException
import com.natu.ftax.transaction.application.TransactionRepository
import com.natu.ftax.transaction.domain.Transaction
import org.springframework.stereotype.Repository


@Repository
class TransactionRepositoryInMemory : TransactionRepository {

    private val transactions = hashMapOf<String, Transaction>()


    override fun getTransactionById(id: String): Transaction {
        return transactions[id] ?: throw NotFoundException("Transaction not found with id: $id")
    }

    override fun getAllTransactions(): Array<Transaction> {
        return transactions.values.toTypedArray()
    }

    override fun save(transaction: Transaction) {
        transactions[transaction.id] = transaction
    }
}