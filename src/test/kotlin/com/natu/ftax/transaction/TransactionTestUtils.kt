package com.natu.ftax.transaction

import com.natu.ftax.transaction.domain.SubmitTransactionCommand
import com.natu.ftax.transaction.domain.Token
import com.natu.ftax.transaction.domain.Transaction
import com.natu.ftax.transaction.domain.TransactionType
import java.util.*

class TransactionTestUtils {

    companion object {

        @JvmStatic
        fun draftTransaction(id: String): Transaction {
            return Transaction.create(id)
        }

        @JvmStatic
        fun submittedTransaction(id: String): Transaction {
            val tx = draftTransaction(id)
            tx.submit(command = SubmitTransactionCommand(
                id = id,
                transactionType = TransactionType.TRANSFER,
                date = Date(),
                tokenIn = "BTC",
                tokenOut = "ETH",
                tokenFee = "BTC",
                amountIn = 1.0,
                amountOut = 2.0,
                amountFee = 0.01,
                externalId = "ext-123"
            ))

            return tx
        }

        @JvmStatic
        fun submittedTransactionWithValue(id: String,
                                            amountIn: Double,
                                            amountOut: Double,
                                            amountFee: Double,
                                            tokenIn: Token = "BTC",
                                            tokenOut: Token = "ETH",
                                            tokenFee: Token = "BTC"): Transaction {

            val tx = draftTransaction(id)
            tx.submit(command = SubmitTransactionCommand(
                id = id,
                transactionType = TransactionType.TRANSFER,
                date = Date(),
                tokenIn = tokenIn,
                tokenOut = tokenOut,
                tokenFee = tokenFee,
                amountIn = amountIn,
                amountOut = amountOut,
                amountFee = amountFee,
                externalId = "ext-123"
            ))

            return tx
        }

    }
}