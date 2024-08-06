package com.natu.ftax.transaction.presentation;

import com.natu.ftax.transaction.application.TransactionService;
import com.natu.ftax.transaction.domain.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @Operation(summary = "Create a draft transaction")
    @PostMapping(
        value = "draft",
        produces = "application/json"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createDraftTransaction() {
        Transaction draftTransaction = service.createTransaction();
        LOGGER.info("Creating a draft transaction with id: {}", draftTransaction.getId());
        return new TransactionResponse(draftTransaction);
    }

    @Operation(summary = "Submit a draft transaction")
    @PostMapping(
        value = "submit",
        consumes = "application/json"
    )
    public ResponseEntity<Void> submitDraftTransaction(@Valid @RequestBody SubmitTransactionRequest request) {
        LOGGER.info("Submitting draft transaction with id: {}", request.getId());
        service.submitDraftTransaction(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Submit a draft transaction")
    @PostMapping(
            value = "submit/{id}"
    )
    public ResponseEntity<Void> submitDraftTransactionById(
            @PathVariable() String id) {
        LOGGER.info("Submitting draft transaction by id: {}", id);
        service.submitDraftTransaction(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Edit a field")
    @PutMapping(
        value = "edit",
        produces = "application/json"
    )
    public TransactionResponse editField(@Valid @RequestBody EditFieldRequest request) {
        LOGGER.info("Editing field with id: {}", request.getId());
        return new TransactionResponse(service.editField(request));
    }

    @Operation(summary = "Edit a transaction")
    @PostMapping(
        value = "edit/{id}",
        produces = "application/json"
    )
    public TransactionResponse editTransaction(@PathVariable() String id) {
        LOGGER.info("Editing transaction with id: {}", id);
        return new TransactionResponse(service.editTransaction(id));
    }

    @Operation(summary = "Get all transactions")
    @GetMapping(
        produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionResponse> getAllTransactions() {
        LOGGER.info("Getting all transactions");
        return service.getAllTransactions().stream().map(
                TransactionResponse::new).toList();
    }

    @Operation(summary = "Delete a transaction")
    @DeleteMapping(
        value = "delete/{id}",
        produces = "application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    public void deleteTransaction(@PathVariable String id) {
        LOGGER.info("Deleting transaction with id: {}", id);
        service.deleteTransaction(id);
    }
}
