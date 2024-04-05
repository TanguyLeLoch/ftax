package com.natu.ftax.ledger.presentation

import com.natu.ftax.ledger.application.LedgerBookService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatus
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
    fun generateLedgerBook() {
        service.generateLedgerBook()
    }


}