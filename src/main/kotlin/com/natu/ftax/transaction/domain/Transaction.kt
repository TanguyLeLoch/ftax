package com.natu.ftax.transaction.domain

import java.util.*


typealias Token = String

class Transaction private constructor(val id: String) {
    var state = TransactionState.DRAFT
        private set
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
        fun create(id: String): Transaction {
            return Transaction(id)
        }
    }


    fun submit(command: SubmitTransactionCommand) {
        checkIsDraft()
        this.transactionType = command.transactionType
        this.date = command.date
        this.token1 = command.token1
        this.token2 = command.token2
        this.tokenFee = command.tokenFee
        this.amount1 = command.amount1
        this.amount2 = command.amount2
        this.amountFee = command.amountFee
        this.externalId = command.externalId
        checkNoNullValues()
        this.state = TransactionState.SUBMITTED
    }

    private fun checkNoNullValues() {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(transactionType != TransactionType.NONE) { "Transaction type cannot be NONE" }
        require(amount1 >= 0) { "Amount1 must not be less than 0" }
        require(amount2 >= 0) { "Amount2 must not be less than 0" }
        require(amountFee >= 0) { "AmountFee must not be less than 0" }
        requireNotNull(date) { "Date cannot be null" }
        requireNotNull(token1) { "Token1 cannot be null" }
        requireNotNull(token2) { "Token2 cannot be null" }
        requireNotNull(tokenFee) { "TokenFee cannot be null" }
    }

    private fun checkIsDraft() {
        if (state != TransactionState.DRAFT) {
            throw IllegalStateException("Cannot change date of a non draft transaction")
        }
    }
}
