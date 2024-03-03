package com.natu.ftax.transaction.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TransactionTest {

    @Test
    fun `should create a transaction from draft transaction`() {
        val draftTransaction = DraftTransaction.create("123") {
            setDate(Date())
            setAmount1(120.0)
            setAmount2(10.0)
            setAmountFee(1.0)
            setToken1("token1")
            setToken2("token2")
            setTokenFee("tokenFee")
            setTransactionType(TransactionType.SWAP)
        }
        val transaction = Transaction.fromDraft(draftTransaction)

        assertEquals("123", transaction.id)
    }
}