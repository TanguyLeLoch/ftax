package com.natu.ftax.transaction.infrastructure

import com.natu.ftax.common.exception.NotFoundException
import com.natu.ftax.transaction.application.TransactionRepository
import com.natu.ftax.transaction.domain.Transaction
import org.springframework.stereotype.Repository


@Repository
class TransactionRepositoryImpl(val transactionRepositoryJpa: TransactionRepositoryJpa) : TransactionRepository {

    override fun getTransactionById(id: String): Transaction {
        return transactionRepositoryJpa.findById(id).map { it.toDomain() }
            .orElseThrow { NotFoundException("Transaction not found with id: $id") }
    }

    override fun getAllTransactions(): Array<Transaction> {
        return transactionRepositoryJpa.findAll().stream().map { it.toDomain() }.toArray { size -> arrayOfNulls(size) }
    }

    override fun save(transaction: Transaction) {
        transactionRepositoryJpa.save(TransactionEntity.fromDomain(transaction))
    }
}