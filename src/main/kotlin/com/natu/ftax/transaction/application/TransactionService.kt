package com.natu.ftax.transaction.application

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.transaction.domain.Transaction
import com.natu.ftax.transaction.presentation.EditTransactionRequest
import com.natu.ftax.transaction.presentation.SubmitTransactionRequest
import org.springframework.stereotype.Service

@Service
class TransactionService(val idGenerator: IdGenerator, val transactionRepository: TransactionRepository) {

    fun createTransaction(): Transaction {
        val transaction = Transaction.create(idGenerator.generate())
        transactionRepository.save(transaction)
        return transaction
    }

    fun getAllTransactions(): Array<Transaction> {
        return transactionRepository.getAllTransactions()
    }

    fun submitDraftTransaction(request: SubmitTransactionRequest) {
        val transaction = transactionRepository.getTransactionById(request.id)
        transaction.submit(request.toCommand())
        transactionRepository.save(transaction)
    }

    fun editField(request: EditTransactionRequest): Transaction {
        val transaction = transactionRepository.getTransactionById(request.id)
        request.toCommand().execute(transaction)
        transactionRepository.save(transaction)
        return transaction
    }

    fun editTransaction(id: String): Transaction {
        val transaction = transactionRepository.getTransactionById(id)
        transaction.edit()
        transactionRepository.save(transaction)
        return transaction
    }

    fun deleteTransaction(id: String) {
        transactionRepository.deleteTransaction(id)
    }
}