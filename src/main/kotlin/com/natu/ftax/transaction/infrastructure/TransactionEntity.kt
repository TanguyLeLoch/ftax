package com.natu.ftax.transaction.infrastructure

import com.natu.ftax.transaction.domain.Transaction
import com.natu.ftax.transaction.domain.TransactionState
import com.natu.ftax.transaction.domain.TransactionType
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "transactions")
class TransactionEntity(

    @Id
    val id: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val state: TransactionState = TransactionState.DRAFT,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val transactionType: TransactionType = TransactionType.NONE,

    @Temporal(TemporalType.TIMESTAMP)
    val date: Date? = null,

    val token1: String? = null,

    val token2: String? = null,

    val tokenFee: String? = null,

    val amount1: Double = 0.0,

    val amount2: Double = 0.0,

    val amountFee: Double = 0.0,

    val externalId: String? = null

) {
    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity {
            return TransactionEntity(
                id = transaction.id,
                state = transaction.state,
                transactionType = transaction.transactionType,
                date = transaction.date,
                token1 = transaction.token1,
                token2 = transaction.token2,
                tokenFee = transaction.tokenFee,
                amount1 = transaction.amount1,
                amount2 = transaction.amount2,
                amountFee = transaction.amountFee,
                externalId = transaction.externalId
            )
        }
    }

    fun toDomain(): Transaction {
        return Transaction(
            id = id!!,
            state = state,
            transactionType = transactionType,
            date = date,
            token1 = token1,
            token2 = token2,
            tokenFee = tokenFee,
            amount1 = amount1,
            amount2 = amount2,
            amountFee = amountFee,
            externalId = externalId
        )
    }
}