package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.transaction.importer.PlatformImporter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class TransactionService {

    private final Map<String, PlatformImporter> platformImporters;

    public TransactionService(Map<String, PlatformImporter> platformImporters) {

        this.platformImporters = platformImporters;

    }

    public void importTransactions(String platform, MultipartFile file,
        Client client) {
        PlatformImporter importer = platformImporters.get(
            platform + "Importer");
        if (importer != null) {
            importer.importTransaction(file, client);
        } else {
            throw new FunctionalException(
                "Platform " + platform + " not supported");
        }
    }
}
