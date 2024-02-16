package com.natu.ftax.transaction.domain

import java.util.*

class DraftTransaction(
    val id: String,
    val transactionType: Transaction.TransactionType?,
    val date: Date?,
    val token1: Token?,
    val token2: Token?,
    val tokenFee: Token?,
    val amount1: Double?,
    val amount2: Double?,
    val amountFee: Double?
) {
    constructor(id: String) : this(id, null, null, null, null, null, 0.0, 0.0, 0.0)
}