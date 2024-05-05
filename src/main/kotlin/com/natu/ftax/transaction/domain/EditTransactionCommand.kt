package com.natu.ftax.transaction.domain

import java.util.*

class EditTransactionCommand(
    val id: String,
    val transactionType: TransactionType?,
    val date: Date?,
    val tokenIn: String?,
    val tokenOut: String?,
    val tokenFee: String?,
    val amountIn: Double?,
    val amountOut: Double?,
    val amountFee: Double?,
    val externalId: String?,
) {
    fun execute(transaction: Transaction) {
        if (transactionType != null) {
            transaction.setTransactionType(transactionType)
            return
        }
        if (date != null) {
            transaction.setDate(date)
            return
        }
        if (tokenIn != null) {
            transaction.setTokenIn(tokenIn)
            return
        }
        if (tokenOut != null) {
            transaction.setTokenOut(tokenOut)
            return
        }
        if (tokenFee != null) {
            transaction.setTokenFee(tokenFee)
            return
        }
        if (amountIn != null) {
            transaction.setAmountIn(amountIn)
            return
        }
        if (amountOut != null) {
            transaction.setAmountOut(amountOut)
            return
        }
        if (amountFee != null) {
            transaction.setAmountFee(amountFee)
            return
        }
        if (externalId != null) {
            transaction.setExternalId(externalId)
            return
        }
    }
}