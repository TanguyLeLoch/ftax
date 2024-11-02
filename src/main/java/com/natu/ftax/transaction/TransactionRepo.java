package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo
        extends JpaRepository<Transaction, String>, TransactionRepositoryCustom {

    List<Transaction> findAllByClient(Client client);

    List<Transaction> findAllByExternalIdAndClient(String externalId, Client client);

    int deleteByExternalIdAndClient(String externalId, Client client);

    int deleteByIdAndClient(String id, Client client);

    boolean existsByIdAndClient(String id, Client client);
}
