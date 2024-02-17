package com.natu.ftax.transaction.presentation

import com.natu.ftax.transaction.application.TransactionService
import com.natu.ftax.transaction.domain.DraftTransaction
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("transaction")
class TransactionController(val service: TransactionService) {

    @PostMapping("draft")
    fun createDraftTransaction(): DraftTransaction {
        return service.createDraftTransaction()
    }
}