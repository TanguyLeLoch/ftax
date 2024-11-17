package com.natu.ftax.transaction.importer;

import java.time.LocalDate;
import com.natu.ftax.client.Client;

public interface OnChainImporter {

    void importTransaction(String address, LocalDate from, LocalDate to,
        Client client);
}
