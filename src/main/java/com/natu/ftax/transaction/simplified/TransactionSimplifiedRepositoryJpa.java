package com.natu.ftax.transaction.simplified;

import com.natu.ftax.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionSimplifiedRepositoryJpa
        extends JpaRepository<TransactionSimplified, String> {

    List<TransactionSimplified> findAllByClient(Client client);
}
