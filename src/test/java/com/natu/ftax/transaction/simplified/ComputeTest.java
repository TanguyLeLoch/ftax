package com.natu.ftax.transaction.simplified;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComputeTest {


    @ParameterizedTest(name = "{index} => amountSold={1}, expectedPnl={2}")
    @MethodSource("provideTransactionsForFifo")
    void testComputeFifo(List<TransactionSimplified> transactions,
        BigDecimal expectedPnl) {
        var compute = new Compute(transactions);
        var txs = compute.execute("fifo");

        assertEquals(0, expectedPnl.compareTo(txs.get(0).getPnl().getValue()));
    }

    private static Stream<Arguments> provideTransactionsForFifo() {
        return Stream.of(Arguments.of(List.of(
                createTransaction(TransactionSimplified.Type.BUY, 1, 30,
                    LocalDate.of(2024, Month.JANUARY, 1)),
                createTransaction(TransactionSimplified.Type.BUY, 2, 60,
                    LocalDate.of(2024, Month.JANUARY, 2)),
                createTransaction(TransactionSimplified.Type.SELL, 1.5, 50,
                    LocalDate.of(2024, Month.JANUARY, 3))), BigDecimal.valueOf(15)),

            Arguments.of(List.of(
                    createTransaction(TransactionSimplified.Type.BUY, 1, 30,
                        LocalDate.of(2024, Month.JANUARY, 1)),
                    createTransaction(TransactionSimplified.Type.BUY, 2, 60,
                        LocalDate.of(2024, Month.JANUARY, 2)),
                    createTransaction(TransactionSimplified.Type.SELL, 2.5, 30,
                        LocalDate.of(2024, Month.JANUARY, 3))),
                BigDecimal.valueOf(-45))
        );
    }

    private static TransactionSimplified createTransaction(
        TransactionSimplified.Type type, double amount, double price,
        LocalDate date) {
        return TransactionSimplified.builder().type(type).token("BTC")
            .amount(BigDecimal.valueOf(amount))
            .price(BigDecimal.valueOf(price))
            .localDateTime(date.atStartOfDay()).build();
    }

    @Test
    void TestComputeAverage() {
        var tx1 = TransactionSimplified.builder()
            .type(TransactionSimplified.Type.BUY)
            .token("BTC")
            .amount(BigDecimal.valueOf(1))
            .price(BigDecimal.valueOf(30))
            .localDateTime(LocalDate.of(2024, Month.JANUARY, 1).atStartOfDay())
            .build();
        var tx2 = TransactionSimplified.builder()
            .token("BTC")
            .type(TransactionSimplified.Type.BUY)
            .amount(BigDecimal.valueOf(2))
            .price(BigDecimal.valueOf(60))
            .localDateTime(LocalDate.of(2024, Month.JANUARY, 2).atStartOfDay())
            .build();
        var tx3 = TransactionSimplified.builder()
            .type(TransactionSimplified.Type.SELL)
            .token("BTC")
            .amount(BigDecimal.valueOf(1.5))
            .price(BigDecimal.valueOf(50))
            .localDateTime(LocalDate.of(2024, Month.JANUARY, 3).atStartOfDay())
            .build();

        var txs = List.of(tx1, tx2, tx3);

        var compute = new Compute(txs);
        var resTxs = compute.execute("average");


        // Check that the calculated PnL matches the expected PnL
        assertEquals(0,
            BigDecimal.ZERO.compareTo(resTxs.get(0).getPnl().getValue()));
    }
}