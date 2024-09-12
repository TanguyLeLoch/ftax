package com.natu.ftax.transaction;

import com.natu.ftax.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo
        extends JpaRepository<Transaction, String> {

    List<Transaction> findAllByClient(Client client);
}
