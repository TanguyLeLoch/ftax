package com.natu.ftax.transaction.domain

class TransactionCommand {

    val transactionType: TransactionType
    val date: Long?
    val token1: Token?
    val token2: Token?
    val tokenFee: Token?
    val amount1: Double?
    val amount2: Double?
    val amountFee: Double?

    constructor(
        transactionType: TransactionType,
        date: Long?,
        token1: Token?,
        token2: Token?,
        tokenFee: Token?,
        amount1: Double?,
        amount2: Double?,
        amountFee: Double?
    ) {
        this.transactionType = transactionType
        this.date = date
        this.token1 = token1
        this.token2 = token2
        this.tokenFee = tokenFee
        this.amount1 = amount1
        this.amount2 = amount2
        this.amountFee = amountFee
    }


}
