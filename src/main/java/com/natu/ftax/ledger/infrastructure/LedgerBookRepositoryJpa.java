package com.natu.ftax.ledger.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerBookRepositoryJpa extends JpaRepository<LedgerBookEntity, String> {
}
