package com.natu.ftax.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.transaction.importer.OnChainImporter;
import com.natu.ftax.transaction.importer.PlatformImporter;

@Service
public class TransactionService {

    private final Map<String, PlatformImporter> platformImporters;
    private final Map<String, OnChainImporter> onChainImporters;
    private final TransactionRepo repo;

    public TransactionService(
            Map<String, PlatformImporter> platformImporters,
            Map<String, OnChainImporter> onChainImporters,
            TransactionRepo repo) {

        this.platformImporters = platformImporters;
        this.onChainImporters = onChainImporters;
        this.repo = repo;

    }


    public void importTransactions(String platform, MultipartFile file,
        Client client) {
        PlatformImporter importer = platformImporters.get(
            platform + "Importer");
        if (importer == null) {
            throw new FunctionalException(
                "Platform " + platform + " not supported");
        }
        importer.importTransaction(file, client);
    }

    public void importOnchainTransactions(String blockchain, String address,
        LocalDate from, LocalDate to, Client client) {
        OnChainImporter importer = onChainImporters.get(
            blockchain + "Importer");
        if (importer == null) {
            throw new FunctionalException(
                "Blockchain " + blockchain + " not supported");
        }
        importer.importTransaction(address, from, to, client);
    }

    public List<Transaction> getAllByClient(Client client, Filter filter) {
        return repo.findAllByClientWithFilter(client, filter);
    }
}
