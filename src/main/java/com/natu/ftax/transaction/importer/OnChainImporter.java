package com.natu.ftax.transaction.importer;

import com.natu.ftax.client.Client;

import java.time.LocalDateTime;

public interface OnChainImporter {

    void importTransaction(String address, LocalDateTime from, LocalDateTime to,
        Client client);
}
