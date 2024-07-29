package com.natu.ftax.ledger.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LedgerBookTest {
    @Test
    void testGenerateSimpleEntry() {
        LedgerBook ledgerBook = LedgerBook.create("1");
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");

        ledgerBook.getLedgerEntries().add(firstLedgerEntry);

        Assertions.assertThat(ledgerBook.getId()).isEqualTo("1");
        Assertions.assertThat(ledgerBook.getLedgerEntries().size()).isEqualTo(1);
    }
}
