package com.natu.ftax.transaction.application

import com.natu.ftax.transaction.domain.DraftTransaction
import com.natu.ftax.transaction.domain.Transaction

interface TransactionRepository {
    fun getTransactionById(id: String): Transaction
    fun getDraftTransactionById(id: String): DraftTransaction
    fun getAllTransactions(): Array<Transaction>
    fun save(transaction: Transaction)
    fun save(transaction: DraftTransaction)
}