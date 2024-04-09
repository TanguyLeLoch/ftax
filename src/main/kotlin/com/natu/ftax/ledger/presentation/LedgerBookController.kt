package com.natu.ftax.ledger.presentation

import com.natu.ftax.ledger.application.LedgerBookService
import com.natu.ftax.ledger.domain.LedgerBook
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("ledger-book")
class LedgerBookController(val service: LedgerBookService) {

    @Operation(summary = "Generate the ledger book")
    @PostMapping(
        value = ["generate"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun generateLedgerBook() : LedgerBook{
        return service.generateLedgerBook()
    }

    @Operation(summary = "Get the ledger book")
    @GetMapping(
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun getLedgerBook(): ResponseEntity<LedgerBook> {
        val ledgerBook = service.getLedgerBook() ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(ledgerBook)
    }

    @Operation(summary = "Delete the ledger book")
    @DeleteMapping(
        value = ["{ledgerBookId}"],
        produces = ["application/json"]
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteLedgerBook(@PathVariable ledgerBookId: String) {
        service.deleteLedgerBook(ledgerBookId)
    }



}