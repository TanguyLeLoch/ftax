package com.natu.ftax.transaction.domain

import java.util.*

class DraftTransaction private constructor(val id: String) {
    var transactionType = TransactionType.NONE
        private set
    var date: Date? = null
        private set
    var token1: Token? = null
        private set
    var token2: Token? = null
        private set
    var tokenFee: Token? = null
        private set
    var amount1 = 0.0
        private set(value) {
            require(value >= 0) { "Amount1 must not be less than 0" }
            field = value
        }
    var amount2 = 0.0
        private set(value) {
            require(value >= 0) { "Amount2 must not be less than 0" }
            field = value
        }
    var amountFee = 0.0
        private set(value) {
            require(value >= 0) { "AmountFee must not be less than 0" }
            field = value
        }

    // string or null
    var externalId: String? = null
        private set

    companion object {
        fun create(id: String, configuration: DraftTransaction.() -> Unit): DraftTransaction {
            return DraftTransaction(id).apply(configuration)
        }
    }

    fun setTransactionType(transactionType: TransactionType) = apply { this.transactionType = transactionType }
    fun setDate(date: Date?) = apply { this.date = date }
    fun setToken1(token1: Token?) = apply { this.token1 = token1 }
    fun setToken2(token2: Token?) = apply { this.token2 = token2 }
    fun setTokenFee(tokenFee: Token?) = apply { this.tokenFee = tokenFee }
    fun setAmount1(amount1: Double) = apply { this.amount1 = amount1 }
    fun setAmount2(amount2: Double) = apply { this.amount2 = amount2 }
    fun setAmountFee(amountFee: Double) = apply { this.amountFee = amountFee }
    fun setExternalId(externalId: String) = apply { this.externalId = externalId }
}
