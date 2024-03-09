package com.natu.ftax.transaction.application

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.transaction.domain.DraftTransaction
import com.natu.ftax.transaction.domain.Transaction
import org.springframework.stereotype.Service

@Service
class TransactionService(val idGenerator: IdGenerator) {

    fun createDraftTransaction(): DraftTransaction {
        return DraftTransaction.create(idGenerator.generate()) { }
    }

    fun getAllTransactions(): Array<Transaction> {
        return emptyArray()
    }

}