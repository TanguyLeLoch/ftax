package com.natu.ftax.transaction.simplified;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.common.exception.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
    @Transactional
    public TransactionSimplified post(
            @RequestBody TransactionSimplified transactionSimplified) {
        defaultValue(transactionSimplified);
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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TransactionSimplified getById(
            @PathVariable(value = "id") String id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Transaction not found"));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionSimplified> getAll() {
        return repository.findAll();
    }

    @PostMapping(value = "computePnl",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TransactionSimplified> computePnl(@RequestParam("method") String method) {
        var txs = getAll();
        var compute = new Compute(txs);
        List<TransactionSimplified> txToSave = compute.execute(method);
        return repository.saveAll(txToSave);
    }
}
