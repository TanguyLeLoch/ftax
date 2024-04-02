package com.natu.ftax.transaction.domain

import com.natu.ftax.common.exception.FunctionalException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

class TransactionTest {

    @Test
    fun `should create a draft transaction`() {
        val draftTransaction = Transaction.create("123")

        assertEquals("123", draftTransaction.id)
        assertEquals(TransactionState.DRAFT, draftTransaction.state)
    }

    @Test
    fun `should create a transaction from draft transaction`() {
        val transaction = Transaction.create("abc")

        transaction.submit(
            SubmitTransactionCommand(
                "abc",
                TransactionType.SWAP,
                Date(),
                "0x123",
                "0x456",
                "0x789",
                120.0,
                0.0,
                0.0,
                "0x124"
            )
        )

        assertEquals("abc", transaction.id)
        assertEquals(TransactionState.SUBMITTED, transaction.state)
        assertEquals(TransactionType.SWAP, transaction.transactionType)
        assertEquals("0x123", transaction.token1)
        assertEquals("0x456", transaction.token2)
        assertEquals("0x789", transaction.tokenFee)
        assertEquals(120.0, transaction.amount1)
        assertEquals(0.0, transaction.amount2)
        assertEquals(0.0, transaction.amountFee)
        assertEquals("0x124", transaction.externalId)

    }

    @ParameterizedTest
    @MethodSource("amountArguments")
    fun `should not be able to submit a transaction with negative amounts`(
        amount1: Double,
        amount2: Double,
        amountFee: Double
    ) {
        val tx = Transaction.create("anId")
        val exception = assertThrows<IllegalArgumentException> {
            tx.submit(
                SubmitTransactionCommand(
                    "anId",
                    TransactionType.SWAP,
                    Date(),
                    "0x123",
                    "0x456",
                    "0x789",
                    amount1,
                    amount2,
                    amountFee,
                    "0x123"
                )
            )
        }



        Assertions.assertThat(exception.message).contains(" must not be less than 0")
    }

    @Test
    fun `should be able to edit a transaction`() {
        val transaction = submittedTransaction()

        transaction.edit()

        assertEquals("abc", transaction.id)
        assertEquals(TransactionState.DRAFT, transaction.state)
    }

    @Test
    fun `should not be able to edit a draft transaction`() {
        val transaction = Transaction.create("abc")

        val exception = assertThrows<FunctionalException> {
            transaction.edit()
        }

        Assertions.assertThat(exception.message).contains("Transaction is not in SUBMITTED state")
    }

    private fun submittedTransaction(): Transaction {
        val transaction = Transaction.create("abc")

        transaction.submit(
            SubmitTransactionCommand(
                "abc",
                TransactionType.SWAP,
                Date(),
                "0x123",
                "0x456",
                "0x789",
                120.0,
                0.0,
                0.0,
                "0x124"
            )
        )
        return transaction
    }


    companion object {
        @JvmStatic
        fun amountArguments(): Stream<Arguments> = Stream.of(
            Arguments.of(-1.0, 0.0, 0.0),
            Arguments.of(0.0, -1.0, 0.0),
            Arguments.of(0.0, 0.0, -1.0)
        )
    }
}