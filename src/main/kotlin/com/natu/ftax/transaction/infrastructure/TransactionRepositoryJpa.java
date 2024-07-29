package com.natu.ftax.transaction.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepositoryJpa extends JpaRepository<TransactionEntity, String> {
}
