package com.natu.ftax.transaction.simplified;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PnlRepo extends JpaRepository<Pnl, String> {
}
