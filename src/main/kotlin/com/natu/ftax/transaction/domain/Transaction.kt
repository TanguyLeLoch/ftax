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
    var tokenIn: Token? = null
        private set
    var tokenOut: Token? = null
        private set
    var tokenFee: Token? = null
        private set
    var amountIn = 0.0
        private set(value) {
            require(value >= 0) { "AmountIn must not be less than 0" }
            field = value
        }
    var amountOut = 0.0
        private set(value) {
            require(value >= 0) { "AmountOut must not be less than 0" }
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
        tokenIn: Token?,
        tokenOut: Token?,
        tokenFee: Token?,
        amountIn: Double,
        amountOut: Double,
        amountFee: Double,
        externalId: String?
    ) : this(id) {
        this.state = state
        this.transactionType = transactionType
        this.date = date
        this.tokenIn = tokenIn
        this.tokenOut = tokenOut
        this.tokenFee = tokenFee
        this.amountIn = amountIn
        this.amountOut = amountOut
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
        this.tokenIn = command.tokenIn
        this.tokenOut = command.tokenOut
        this.tokenFee = command.tokenFee
        this.amountIn = command.amountIn
        this.amountOut = command.amountOut
        this.amountFee = command.amountFee
        this.externalId = command.externalId
        checkNoNullValues()
        this.state = TransactionState.SUBMITTED
    }

    private fun checkNoNullValues() {
        require(id.isNotBlank()) { "ID cannot be blank" }
        require(transactionType != TransactionType.NONE) { "Transaction type cannot be NONE" }
        require(amountIn >= 0) { "AmountIn must not be less than 0" }
        require(amountOut >= 0) { "AmountOut must not be less than 0" }
        require(amountFee >= 0) { "AmountFee must not be less than 0" }
        requireNotNull(date) { "Date cannot be null" }
        requireNotNull(tokenIn) { "TokenIn cannot be null" }
        requireNotNull(tokenOut) { "TokenOut cannot be null" }
        requireNotNull(tokenFee) { "TokenFee cannot be null" }
    }

    fun edit() {
        if (state != TransactionState.SUBMITTED) {
            throw FunctionalException("Transaction is not in SUBMITTED state")
        }
        this.state = TransactionState.DRAFT
    }
}
