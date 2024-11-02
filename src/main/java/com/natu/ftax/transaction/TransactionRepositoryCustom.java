package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;

import java.util.List;

public interface TransactionRepositoryCustom {
    List<Transaction> findAllByClientWithFilter(Client client, Filter filter);
}