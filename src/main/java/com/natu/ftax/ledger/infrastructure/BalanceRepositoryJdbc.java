package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.Balance;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class BalanceRepositoryJdbc implements BalanceRepository {

    private final BalanceRepositoryJpa balanceRepositoryJpa;

    public BalanceRepositoryJdbc(BalanceRepositoryJpa balanceRepositoryJpa) {
        this.balanceRepositoryJpa = balanceRepositoryJpa;
    }

    @Override
    public void save(Collection<Balance> balances) {
        balanceRepositoryJpa.saveAll(balances.stream().map(BalanceEntity::fromDomain).toList());
    }

    @Override
    public void cleanOrphanBalances() {
        balanceRepositoryJpa.cleanOrphanBalances();
    }
}
