package com.natu.ftax.transaction.application

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.transaction.domain.DraftTransaction
import com.natu.ftax.transaction.domain.Transaction
import com.natu.ftax.transaction.presentation.EditTransactionRequest
import org.springframework.stereotype.Service

@Service
class TransactionService(val idGenerator: IdGenerator, val transactionRepository: TransactionRepository) {


    fun createDraftTransaction(): DraftTransaction {
        val draftTransaction = DraftTransaction.create(idGenerator.generate()) { }
        transactionRepository.save(draftTransaction)
        return draftTransaction
    }

    fun getAllTransactions(): Array<Transaction> {
        return transactionRepository.getAllTransactions()
    }

    fun submitDraftTransaction(request: EditTransactionRequest) {

        val draftTransaction = transactionRepository.getDraftTransactionById(request.id)
        draftTransaction.edit(
            request.transactionType,
            request.date,
            request.token1,
            request.token2,
            request.tokenFee,
            request.amount1,
            request.amount2,
            request.amountFee,
            request.externalId
        )
        val transaction = Transaction.fromDraft(draftTransaction)

        transactionRepository.save(transaction)
    }
}