package com.natu.ftax.transaction.simplified;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.natu.ftax.transaction.simplified.TransactionSimplified.Type.BUY;

@RestController
@RequestMapping("/api/transaction-simplified")

public class TransactionSimplifiedController {
    private final TransactionSimplifiedRepositoryJpa repository;
    private final IdGenerator idGenerator;


    public TransactionSimplifiedController(
            IdGenerator idGenerator,
            TransactionSimplifiedRepositoryJpa repository) {
        this.repository = repository;
        this.idGenerator = idGenerator;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isConnected()")
    @Transactional
    public TransactionSimplified post(
            @RequestBody TransactionSimplified transactionSimplified, Principal principal) {
        defaultValue(transactionSimplified);
        Client client = getClient(principal);
        transactionSimplified.setClient(client);
        return repository.save(transactionSimplified);
    }

    private void defaultValue(TransactionSimplified transactionSimplified) {
        if (transactionSimplified.getId() == null) {
            transactionSimplified.setId(idGenerator.generate());
        }
        if (transactionSimplified.getLocalDateTime() == null) {
            transactionSimplified.setLocalDateTime(
                    LocalDateTime.now(ZoneId.of("UTC")));
        }
        if (transactionSimplified.getType() == null) {
            transactionSimplified.setType(BUY);
        }
    }

    @PreAuthorize("isConnected()")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionSimplified getById(
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
    public List<TransactionSimplified> getAll(Principal principal) {

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
    public List<TransactionSimplified> computePnl(@RequestParam("method") String method, Principal principal) {
        var txs = getAll(principal);
        var compute = new Compute(txs);
        List<TransactionSimplified> txToSave = compute.execute(method);
        return repository.saveAll(txToSave);
    }
}
