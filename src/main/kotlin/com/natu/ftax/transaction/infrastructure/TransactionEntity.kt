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

    val tokenIn: String? = null,

    val tokenOut: String? = null,

    val tokenFee: String? = null,

    val amountIn: Double = 0.0,

    val amountOut: Double = 0.0,

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
                tokenIn = transaction.tokenIn,
                tokenOut = transaction.tokenOut,
                tokenFee = transaction.tokenFee,
                amountIn = transaction.amountIn,
                amountOut = transaction.amountOut,
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
            tokenIn = tokenIn,
            tokenOut = tokenOut,
            tokenFee = tokenFee,
            amountIn = amountIn,
            amountOut = amountOut,
            amountFee = amountFee,
            externalId = externalId
        )
    }
}