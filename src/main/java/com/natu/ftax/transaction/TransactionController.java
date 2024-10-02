package com.natu.ftax.transaction;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.SuccessResponse;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.calculation.Compute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.natu.ftax.transaction.Transaction.Type.BUY;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {
    private final TransactionRepo repository;
    private final TransactionService service;
    private final IdGenerator idGenerator;


    public TransactionController(
            IdGenerator idGenerator,
        TransactionService service,
            TransactionRepo repository) {
        this.repository = repository;
        this.service = service;
        this.idGenerator = idGenerator;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isConnected()")
    @Transactional
    public Transaction post(
            @RequestBody Transaction transaction, Principal principal) {
        defaultValue(transaction);
        Client client = getClient(principal);
        transaction.setClient(client);
        return repository.save(transaction);
    }

    private void defaultValue(Transaction transaction) {
        if (transaction.getId() == null) {
            transaction.setId(idGenerator.generate());
        }
        if (transaction.getLocalDateTime() == null) {
            transaction.setLocalDateTime(
                    LocalDateTime.now(ZoneId.of("UTC")));
        }
        if (transaction.getType() == null) {
            transaction.setType(BUY);
        }
    }

    @PreAuthorize("isConnected()")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Transaction getById(
            @PathVariable(value = "id") String id, Principal principal) {
        Client client = getClient(principal);

        var tx = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Transaction not found"));
        if (!tx.getClient().getEmail().equals(client.getEmail())) {
            throw new NotFoundException("Transaction not found");
        }
        return tx;

    }

    @PreAuthorize("isConnected()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> getAll(Principal principal) {

        Client client = getClient(principal);
        return repository.findAllByClient(client);
    }

    private static Client getClient(Principal principal) {
        UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) principal;
        return (Client) upat.getPrincipal();
    }

    @PreAuthorize("isConnected()")
    @PostMapping(value = "computePnl",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Transaction> computePnl(@RequestParam("method") String method, Principal principal) {
        var txs = getAll(principal);
        txs.forEach(tx -> tx.setPnl(null));
        var compute = new Compute(txs);
        compute.execute(method);
        return repository.saveAll(txs);
    }

    @Operation(summary = "Import transactions from files")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transactions imported successfully",
            content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content)
    })
    @PostMapping(
        value = "file-import",
        consumes = "multipart/form-data"

    )
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessResponse importTransactions(
        @RequestParam("file") MultipartFile file,
        @RequestParam("platform") String platform,
        Principal principal
    ) {
        Client client = getClient(principal);
        service.importTransactions(platform, file, client);

        return new SuccessResponse(true);
    }
}
