package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.Balance
import org.springframework.stereotype.Repository

@Repository
class BalanceRepositoryJdbc(val balanceRepositoryJpa : BalanceRepositoryJpa): BalanceRepository {
    override fun save(balances: Collection<Balance>) {
        balanceRepositoryJpa.saveAll(balances.map { BalanceEntity.fromDomain(it) })
    }
}