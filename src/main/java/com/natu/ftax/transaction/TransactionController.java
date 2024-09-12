package com.natu.ftax.transaction;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.calculation.Compute;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.natu.ftax.transaction.Transaction.Type.BUY;

@RestController
@RequestMapping("/api/transactions")

public class TransactionController {
    private final TransactionRepo repository;
    private final IdGenerator idGenerator;


    public TransactionController(
            IdGenerator idGenerator,
            TransactionRepo repository) {
        this.repository = repository;
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
}
