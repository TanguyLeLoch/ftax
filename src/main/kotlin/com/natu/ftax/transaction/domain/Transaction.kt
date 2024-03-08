package com.natu.ftax.transaction.domain


import java.util.*

typealias Token = String

class Transaction private constructor(
    val id: String,
    val transactionType: TransactionType,
    val date: Date,
    val token1: Token,
    val token2: Token,
    val tokenFee: Token,
    val amount1: Double,
    val amount2: Double,
    val amountFee: Double,
    val externalId: String?
) {
    init {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(transactionType != TransactionType.NONE) { "Transaction type cannot be NONE" }
        require(amount1 >= 0) { "Amount1 must not be less than 0" }
        require(amount2 >= 0) { "Amount2 must not be less than 0" }
        require(amountFee >= 0) { "AmountFee must not be less than 0" }
    }

    companion object {
        fun fromDraft(draftTransaction: DraftTransaction): Transaction {
            requireNotNull(draftTransaction.date) { "Date cannot be null" }
            requireNotNull(draftTransaction.token1) { "Token1 cannot be null" }
            requireNotNull(draftTransaction.token2) { "Token2 cannot be null" }
            requireNotNull(draftTransaction.tokenFee) { "TokenFee cannot be null" }

            return Transaction(
                id = draftTransaction.id,
                transactionType = draftTransaction.transactionType.takeUnless { it == TransactionType.NONE }
                    ?: throw IllegalArgumentException("Transaction type cannot be NONE"),
                date = draftTransaction.date!!,
                token1 = draftTransaction.token1!!,
                token2 = draftTransaction.token2!!,
                tokenFee = draftTransaction.tokenFee!!,
                amount1 = draftTransaction.amount1,
                amount2 = draftTransaction.amount2,
                amountFee = draftTransaction.amountFee,
                externalId = draftTransaction.externalId
            )
        }
    }
}
