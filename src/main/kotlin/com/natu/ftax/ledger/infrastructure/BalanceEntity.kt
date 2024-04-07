package com.natu.ftax.ledger.infrastructure

import com.natu.ftax.ledger.domain.Balance
import com.natu.ftax.transaction.domain.Token
import jakarta.persistence.*

@Entity
@Table(name = "balances")
class BalanceEntity (
    @Id
    val id: String,
    val amount: Double,
    val token: Token
) {
    // No-arg constructor required by JPA
    protected constructor() : this(id = "dum", amount = 0.0, token = "")
    companion object {
        fun fromDomain(balance: Balance): BalanceEntity {
            return BalanceEntity(balance.id, balance.amount, balance.token)
        }
    }

    fun toDomain(): Balance {
        return Balance(id, amount, token)
    }

}