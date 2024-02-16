package com.natu.ftax.transaction.application

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.transaction.domain.DraftTransaction
import org.springframework.stereotype.Service

@Service
class TransactionService(val idGenerator: IdGenerator) {

    fun createDraftTransaction(): DraftTransaction {
        return DraftTransaction(idGenerator.generate())
    }


}