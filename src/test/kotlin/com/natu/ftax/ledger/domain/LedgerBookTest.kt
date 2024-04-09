package com.natu.ftax.ledger.domain


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LedgerBookTest {
    @Test
    fun testGenerateSimpleEntry() {
        val ledgerBook = LedgerBook.create("1")
        val firstLedgerEntry = LedgerEntry.first("first")

        ledgerBook.ledgerEntries.add(firstLedgerEntry)

        assertThat(ledgerBook.id).isEqualTo("1")
        assertThat(ledgerBook.ledgerEntries.size).isEqualTo(1)

    }


}
