package com.natu.ftax.transaction.application

import com.natu.ftax.transaction.domain.Transaction

interface TransactionRepository {
    fun getTransactionById(id: String): Transaction
    fun getAllTransactions(): Array<Transaction>
    fun save(transaction: Transaction)
}