package com.natu.ftax.transaction;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.SuccessResponse;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.calculation.Compute;
import com.natu.ftax.transaction.importer.eth.EthereumImporter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
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
    private final EthereumImporter ethereumImporter;


    public TransactionController(
            IdGenerator idGenerator,
            TransactionService service,
            TransactionRepo repository,
            EthereumImporter ethereumImporter
    ) {
        this.repository = repository;
        this.service = service;
        this.idGenerator = idGenerator;
        this.ethereumImporter = ethereumImporter;
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
        if (transaction.getPlatform() == null) {
            transaction.setPlatform("Ftax");
        }
        if (transaction.getExternalId() == null) {
            transaction.setExternalId("external- " + transaction.getId());
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

    @PreAuthorize("isConnected()")
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

    @PreAuthorize("isConnected()")
    @PostMapping(value = "onchain-import",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse importOnchainTransactions(
        @RequestParam("blockchain") String blockchain,
        @RequestParam("address") String address,
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        Principal principal
    ) {
        Client client = getClient(principal);
        if (from.isAfter(to)) {
            throw new FunctionalException(
                "Invalid block range: start date must be before end date");
        }
        address = address.toLowerCase();

        service.importOnchainTransactions(blockchain, address, from.atStartOfDay(), to.atTime(23, 59, 59),
            client);

        return new SuccessResponse(true);
    }

    @PreAuthorize("isConnected()")
    @PostMapping(value = "refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public List<Transaction> refresh(@RequestParam("txId") String externalId,
                                     Principal principal) {
        Client client = getClient(principal);

        List<Transaction> txs = repository.findAllByExternalIdAndClient(externalId, client);
        int n = repository.deleteByExternalIdAndClient(externalId, client);
        if (n == 0) {
            throw new NotFoundException("Transaction(s) not found");
        }
        if (!"Ethereum".equals(txs.get(0).getPlatform())) {
            throw new FunctionalException("Transaction is not Ethereum");
        }

        return ethereumImporter.refreshTx(txs.get(0), client);
    }

    @PreAuthorize("isConnected()")
    @DeleteMapping(value = "/externalId/{externalId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void deleteByExternalId(@PathVariable("externalId") String externalId, Principal principal) {
        Client client = getClient(principal);

        int n = repository.deleteByExternalIdAndClient(externalId, client);
        if (n == 0) {
            throw new NotFoundException("Transaction(s) not found");
        }
    }

    @PreAuthorize("isConnected()")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public void deleteById(@PathVariable("id") String id, Principal principal) {
        Client client = getClient(principal);
        int n = repository.deleteByIdAndClient(id, client);
        if (n == 0) {
            throw new NotFoundException("Transaction not found");
        }
    }
}
