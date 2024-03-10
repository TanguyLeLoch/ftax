package com.natu.ftax.transaction.presentation

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.natu.ftax.transaction.domain.SubmitTransactionCommand
import com.natu.ftax.transaction.domain.TransactionType
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import org.jetbrains.annotations.NotNull
import java.util.*


class SubmitTransactionRequest(

    @field:NotNull(value = "Id cannot be null")
    val id: String,

    @field:NotNull(value = "Transaction type cannot be null")
    @JsonProperty("transactionType")
    val transactionType: TransactionType,

    @field:PastOrPresent(message = "Date must be in the past or present")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val date: Date,

    val token1: String,
    val token2: String,
    val tokenFee: String,

    @field:PositiveOrZero(message = "Amount must be positive")
    val amount1: Double,

    @field:PositiveOrZero(message = "Amount must be positive")
    val amount2: Double,

    @field:PositiveOrZero(message = "Fee must be positive")
    val amountFee: Double,

    val externalId: String?


) {
    fun toCommand() = SubmitTransactionCommand(
        id = id,
        transactionType = transactionType,
        date = date,
        token1 = token1,
        token2 = token2,
        tokenFee = tokenFee,
        amount1 = amount1,
        amount2 = amount2,
        amountFee = amountFee,
        externalId = externalId
    )
}
