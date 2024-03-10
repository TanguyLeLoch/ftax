package com.natu.ftax.transaction.presentation

import com.natu.ftax.transaction.application.TransactionService
import com.natu.ftax.transaction.domain.Transaction
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("transaction")
class TransactionController(val service: TransactionService) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TransactionController::class.java)
    }


    @Operation(summary = "Create a draft transaction")
    @PostMapping(
        value = ["draft"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun createDraftTransaction(): Transaction {
        val draftTransaction = service.createTransaction()
        LOGGER.info("Creating a draft transaction with id: ${draftTransaction.id}")
        return draftTransaction
    }

    @Operation(summary = "Submit a draft transaction")
    @PostMapping(
        value = ["submit"],
        consumes = ["application/json"]
    )
    fun submitDraftTransaction(@Valid @RequestBody request: SubmitTransactionRequest) {
        LOGGER.info("Submitting draft transaction with id: ${request.id}")
        return service.submitDraftTransaction(request)
    }

    @Operation(summary = "Get all transactions")
    @GetMapping(
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getAllTransactions(): Array<Transaction> {
        LOGGER.info("Getting all transactions")
        return service.getAllTransactions()
    }
}