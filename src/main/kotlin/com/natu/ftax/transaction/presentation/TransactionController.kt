package com.natu.ftax.transaction.presentation

import com.natu.ftax.transaction.application.TransactionService
import com.natu.ftax.transaction.domain.DraftTransaction
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("transaction")
class TransactionController(val service: TransactionService) {

    @Operation(summary = "Create a draft transaction")
    @PostMapping("draft")
    fun createDraftTransaction(): DraftTransaction {
        return service.createDraftTransaction()
    }
}