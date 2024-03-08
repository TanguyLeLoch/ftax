package com.natu.ftax.transaction.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DraftTransactionTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `should create a draft transaction`() {
        val draftTransaction = DraftTransaction.create("123") { }
        assertEquals("123", draftTransaction.id)
    }

    @Test
    fun `should create a draft transaction with transaction type`() {
        val draftTransaction =
            DraftTransaction.create("123") {
                setTransactionType(TransactionType.SWAP).setAmount1(120.0).setAmount2(0.0)
            }
        assertEquals(TransactionType.SWAP, draftTransaction.transactionType)
        assertEquals(120.0, draftTransaction.amount1)
        assertEquals(0.0, draftTransaction.amount2)
    }

    @Test
    fun `should create a draft transaction with an externalId`() {
        val draftTransaction =
            DraftTransaction.create("123") {
                setExternalId("0x123")
            }
        assertEquals("0x123", draftTransaction.externalId)
    }

    @ParameterizedTest
    @MethodSource("amountArguments")
    fun `should not be able to create a draft transaction with negative amounts`(
        amount1: Double,
        amount2: Double,
        amountFee: Double
    ) {
        val exception = assertThrows<IllegalArgumentException> {
            DraftTransaction.create("123") {
                setAmount1(amount1)
                setAmount2(amount2)
                setAmountFee(amountFee)
            }
        }

        assertThat(exception.message).contains(" must not be less than 0")
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