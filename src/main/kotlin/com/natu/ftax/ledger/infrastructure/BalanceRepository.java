package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.Balance;

import java.util.Collection;

public interface BalanceRepository {
    void save(Collection<Balance> balances);

    void cleanOrphanBalances();
}
