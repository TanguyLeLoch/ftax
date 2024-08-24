package com.natu.ftax.ledger.presentation;

import com.natu.ftax.ledger.application.LedgerBookService;
import com.natu.ftax.ledger.domain.LedgerBook;
import com.natu.ftax.transaction.domain.Token;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @GetMapping(
            value = "{bookId}/tokens",
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public Set<Token> getTokens(@PathVariable String bookId) {
        LedgerBook ledgerBook = service.getLedgerBook();
        return ledgerBook.getTokens();
    }

    @GetMapping(
            value = "timeline/{bookId}/{tokenId}",
            produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public List<TimelineItem> getTimelineForToken(
            @PathVariable String bookId,
            @PathVariable String tokenId) {

        LedgerBook ledgerBook = service.getLedgerBook();
        return new TimeLine(ledgerBook).getTimeLine(tokenId);

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