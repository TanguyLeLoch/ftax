package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.transaction.importer.OnChainImporter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component("EthereumImporter")
public class EthereumImporter implements OnChainImporter {

    private final EtherscanApi etherscanApi;

    public EthereumImporter(EtherscanApi etherscanApi) {
        this.etherscanApi = etherscanApi;
    }

    @Override
    public void importTransaction(String address, LocalDateTime from,
        LocalDateTime to, Client client) {
        long fromTimestamp = from.toEpochSecond(ZoneOffset.UTC);
        long toTimestamp = to.toEpochSecond(ZoneOffset.UTC);

        Integer startBlock = etherscanApi.getBlockNumberByTimestamp(
            fromTimestamp, "after");
        Integer endBlock = etherscanApi.getBlockNumberByTimestamp(toTimestamp,
            "before");

        if (startBlock == null || endBlock == null) {
            throw new FunctionalException(
                "Invalid block range, failed to get block number from Etherscan API");
        }

        List<EtherscanApi.Transaction> transactions = etherscanApi.getTransactions(
            address,
            startBlock,
            endBlock,
            1,
            10000,
            "asc");

        // Process transactions as needed
        for (EtherscanApi.Transaction tx : transactions) {
            // Implement your logic here, e.g., saving to database
        }
    }
}
