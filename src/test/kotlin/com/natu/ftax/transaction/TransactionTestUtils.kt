package com.natu.ftax.transaction

import com.natu.ftax.transaction.domain.SubmitTransactionCommand
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
                token1 = "BTC",
                token2 = "ETH",
                tokenFee = "BTC",
                amount1 = 1.0,
                amount2 = 2.0,
                amountFee = 0.1,
                externalId = "ext-123"
            ))

            return tx
        }

    }
}