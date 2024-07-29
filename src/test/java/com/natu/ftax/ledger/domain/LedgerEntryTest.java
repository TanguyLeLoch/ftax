package com.natu.ftax.ledger.domain;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.IDgenerator.infrastructure.SequentialIdGenerator;
import com.natu.ftax.transaction.TransactionTestUtils;
import com.natu.ftax.transaction.domain.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LedgerEntryTest {

    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new SequentialIdGenerator();
    }

    @Test
    void testGenerateLedgerBook() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue(
                "tx1", 1.0, 5.0, 0.1, "token1", "token2", "token3");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry,
                tx1, idGenerator);

        Assertions.assertThat(ledgerEntry.getId()).isEqualTo("second");
        Assertions.assertThat(ledgerEntry.getBalances().size()).isEqualTo(3);
        Assertions.assertThat(
                        ledgerEntry.getBalances().get(tx1.getTokenIn()).getAmount())
                .isEqualTo(1.0);
        Assertions.assertThat(
                        ledgerEntry.getBalances().get(tx1.getTokenOut()).getAmount())
                .isEqualTo(-5.0);
        Assertions.assertThat(
                        ledgerEntry.getBalances().get(tx1.getTokenFee()).getAmount())
                .isEqualTo(0.1);
    }

    @Test
    void testGenerateLedgerBookWithRealistSwap() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");

        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue(
                "tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry,
                tx1, idGenerator);

        Assertions.assertThat(ledgerEntry.getId()).isEqualTo("second");
        Assertions.assertThat(ledgerEntry.getBalances().size()).isEqualTo(2);
        Assertions.assertThat(ledgerEntry.getBalances().get("BTC").getAmount())
                .isEqualTo(1.1);
        Assertions.assertThat(ledgerEntry.getBalances().get("ETH").getAmount())
                .isEqualTo(-5.0);
    }

    @Test
    void testGenerateLedgerBookWithRealistSwap2() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue(
                "tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry,
                tx1, idGenerator);
        Transaction tx2 = TransactionTestUtils.submittedTransactionWithValue(
                "tx2", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2,
                idGenerator);

        Assertions.assertThat(ledgerEntry2.getId()).isEqualTo("third");
        Assertions.assertThat(ledgerEntry2.getBalances().size()).isEqualTo(2);
        Assertions.assertThat(ledgerEntry2.getBalances().get("BTC").getAmount())
                .isEqualTo(2.2);
        Assertions.assertThat(ledgerEntry2.getBalances().get("ETH").getAmount())
                .isEqualTo(-10.0);
    }

    @Test
    void shouldNotContainTokensWithZeroBalance() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue(
                "tx1", 1.0, 5.0, 0.0, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry,
                tx1, idGenerator);
        Transaction tx2 = TransactionTestUtils.submittedTransactionWithValue(
                "tx2", 5.0, 1.0, 0.0, "ETH", "BTC", "BTC");
        LedgerEntry ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2,
                idGenerator);

        ledgerEntry2.getBalances().forEach((key, value) -> {
            System.out.println(
                    key + " " + value.getAmount() + " " + value.getToken());
        });

        Assertions.assertThat(ledgerEntry2.getBalances().size()).isEqualTo(0);
    }
}
