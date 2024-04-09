package com.natu.ftax.ledger.domain

import com.natu.ftax.IDgenerator.domain.IdGenerator
import com.natu.ftax.IDgenerator.infrastructure.SequentialIdGenerator
import com.natu.ftax.transaction.TransactionTestUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LedgerEntryTest {

    private lateinit var idGenerator: IdGenerator

    @BeforeEach
    fun setUp() {
        idGenerator = SequentialIdGenerator()
    }

    @Test
    fun testGenerateLedgerBook() {
        val firstLedgerEntry = LedgerEntry.first("first")
        val tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "token1", "token2", "token3")
        val ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator)


        Assertions.assertThat(ledgerEntry.id).isEqualTo("second")
        Assertions.assertThat(ledgerEntry.balances.size).isEqualTo(3)
        Assertions.assertThat(ledgerEntry.balances[tx1.tokenIn]?.amount).isEqualTo(1.0)
        Assertions.assertThat(ledgerEntry.balances[tx1.tokenOut]?.amount).isEqualTo(-5.0)
        Assertions.assertThat(ledgerEntry.balances[tx1.tokenFee]?.amount).isEqualTo(0.1)

    }

    @Test
    fun testGenerateLedgerBookWithRealistSwap() {
        val firstLedgerEntry = LedgerEntry.first("first")
        val tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC")
        val ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator)


        Assertions.assertThat(ledgerEntry.id).isEqualTo("second")
        Assertions.assertThat(ledgerEntry.balances.size).isEqualTo(2)
        Assertions.assertThat(ledgerEntry.balances["BTC"]?.amount).isEqualTo(1.1)
        Assertions.assertThat(ledgerEntry.balances["ETH"]?.amount).isEqualTo(-5.0)
    }

    @Test
    fun testGenerateLedgerBookWithRealistSwap2() {
        val firstLedgerEntry = LedgerEntry.first("first")
        val tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC")
        val ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator)
        val tx2 = TransactionTestUtils.submittedTransactionWithValue("tx2", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC")
        val ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2, idGenerator)

        Assertions.assertThat(ledgerEntry2.id).isEqualTo("third")
        Assertions.assertThat(ledgerEntry2.balances.size).isEqualTo(2)
        Assertions.assertThat(ledgerEntry2.balances["BTC"]?.amount).isEqualTo(2.2)
        Assertions.assertThat(ledgerEntry2.balances["ETH"]?.amount).isEqualTo(-10.0)
    }

    @Test
    fun ` The ledger entry should not contain tokens that have a balance of 0`() {
        val firstLedgerEntry = LedgerEntry.first("first")
        val tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.0, "BTC", "ETH", "BTC")
        val ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator)
        val tx2 = TransactionTestUtils.submittedTransactionWithValue("tx2", 5.0, 1.0, 0.0, "ETH", "BTC", "BTC")
        val ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2, idGenerator)

        for (balance in ledgerEntry2.balances) {
            println(balance.key + " " + balance.value.amount + " " + balance.value.token)
        }

        Assertions.assertThat(ledgerEntry2.balances.size).isEqualTo(0)


    }
}