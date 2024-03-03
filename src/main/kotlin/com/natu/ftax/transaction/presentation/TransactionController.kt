package com.natu.ftax.transaction.presentation

import com.natu.ftax.transaction.application.TransactionService
import com.natu.ftax.transaction.domain.DraftTransaction
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("transaction")
class TransactionController(val service: TransactionService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionController::class.java)
    }

    @Operation(summary = "Create a draft transaction")
    @PostMapping("draft")
    @ResponseStatus(HttpStatus.CREATED)
    fun createDraftTransaction(): DraftTransaction {
        val draftTransaction = service.createDraftTransaction()
        LOGGER.info("Creating a draft transaction with id: ${draftTransaction.id}")
        return draftTransaction
    }
}