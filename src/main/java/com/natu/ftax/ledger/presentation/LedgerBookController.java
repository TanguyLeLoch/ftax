package com.natu.ftax.ledger.presentation;

import com.natu.ftax.ledger.application.LedgerBookService;
import com.natu.ftax.ledger.domain.LedgerBook;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ledger-book")
public class LedgerBookController {

    private final LedgerBookService service;

    public LedgerBookController(LedgerBookService service) {
        this.service = service;
    }

    @Operation(summary = "Generate the ledger book")
    @PostMapping(
            value = "generate",
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public LedgerBook generateLedgerBook() {
        return service.generateLedgerBook();
    }

    @Operation(summary = "Get the ledger book")
    @GetMapping(
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LedgerBook> getLedgerBook() {
        LedgerBook ledgerBook = service.getLedgerBook();
        if (ledgerBook == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ledgerBook);
    }

    @Operation(summary = "Delete the ledger book")
    @DeleteMapping(
            value = "{ledgerBookId}",
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLedgerBook(@PathVariable String ledgerBookId) {
        service.deleteLedgerBook(ledgerBookId);
    }
}