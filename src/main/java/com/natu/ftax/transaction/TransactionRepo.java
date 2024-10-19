package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo
        extends JpaRepository<Transaction, String> {

    List<Transaction> findAllByClient(Client client);

    void deleteByExternalId(String hash);

    Optional<Transaction> findByIdAndClient(String txId, Client client);
}
