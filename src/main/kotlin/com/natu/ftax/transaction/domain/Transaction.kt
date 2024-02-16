package com.natu.ftax.transaction.domain

import java.util.Date

typealias Token = String

class Transaction(
        val id: String,
        val transactionType: TransactionType,
        val date: Date,
        val token1: Token,
        val token2: Token,
        val tokenFee: Token,
        val amount1: Double,
        val amount2: Double,
        val amountFee: Double
){
    enum class TransactionType {
        TRANSFER, SWAP
    }
}