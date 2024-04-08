package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.Balance

interface BalanceRepository {
    fun save(balances: Collection<Balance>)
}