package com.natu.ftax.transaction.domain

import com.natu.ftax.common.exception.FunctionalException
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

    constructor(
        id: String,
        state: TransactionState,
        transactionType: TransactionType,
        date: Date?,
        token1: Token?,
        token2: Token?,
        tokenFee: Token?,
        amount1: Double,
        amount2: Double,
        amountFee: Double,
        externalId: String?
    ) : this(id) {
        this.state = state
        this.transactionType = transactionType
        this.date = date
        this.token1 = token1
        this.token2 = token2
        this.tokenFee = tokenFee
        this.amount1 = amount1
        this.amount2 = amount2
        this.amountFee = amountFee
        this.externalId = externalId
    }

    var externalId: String? = null
        private set

    companion object {
        fun create(id: String): Transaction {
            return Transaction(id)
        }
    }


    fun submit(command: SubmitTransactionCommand) {
        if (state != TransactionState.DRAFT) {
            throw FunctionalException("Transaction is not in DRAFT state")
        }
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
}
