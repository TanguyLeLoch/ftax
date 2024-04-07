package com.natu.ftax.transaction.domain

import java.util.*

class SubmitTransactionCommand(
    val id: String,
    val transactionType: TransactionType,
    val date: Date,
    val tokenIn: String,
    val tokenOut: String,
    val tokenFee: String,
    val amountIn: Double,
    val amountOut: Double,
    val amountFee: Double,
    val externalId: String?,
)