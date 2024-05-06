package com.natu.ftax.transaction.presentation

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.natu.ftax.transaction.domain.EditTransactionCommand
import com.natu.ftax.transaction.domain.TransactionType
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import org.jetbrains.annotations.NotNull
import java.util.*


class EditFieldRequest(

    @field:NotNull(value = "Id cannot be null")
    val id: String,

    @JsonProperty("transactionType")
    val transactionType: TransactionType?,

    @field:PastOrPresent(message = "Date must be in the past or present")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val date: Date?,

    val tokenIn: String?,
    val tokenOut: String?,
    val tokenFee: String?,

    @field:PositiveOrZero(message = "Amount must be positive")
    val amountIn: Double?,

    @field:PositiveOrZero(message = "Amount must be positive")
    val amountOut: Double?,

    @field:PositiveOrZero(message = "Fee must be positive")
    val amountFee: Double?,

    val externalId: String?


) {
    fun toCommand() = EditTransactionCommand(
        id = id,
        transactionType = transactionType,
        date = date,
        tokenIn = tokenIn,
        tokenOut = tokenOut,
        tokenFee = tokenFee,
        amountIn = amountIn,
        amountOut = amountOut,
        amountFee = amountFee,
        externalId = externalId
    )
}
