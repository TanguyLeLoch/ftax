package com.natu.ftax.transaction.domain

import java.util.*

class SubmitTransactionCommand(
    val id: String,
    val transactionType: TransactionType,
    val date: Date,
    val token1: String,
    val token2: String,
    val tokenFee: String,
    val amount1: Double,
    val amount2: Double,
    val amountFee: Double,
    val externalId: String?,
)