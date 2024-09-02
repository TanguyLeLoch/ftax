package com.natu.ftax.transaction.simplified;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComputeTest {


    @Test
    void TestComputeFifo() {
        var tx1 = TransactionSimplified.builder()
                .type(TransactionSimplified.Type.BUY)
                .token("BTC")
                .amount(BigDecimal.valueOf(1))
                .dollarValue(BigDecimal.valueOf(30))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 1).atStartOfDay())
                .build();
        var tx2 = TransactionSimplified.builder()
                .token("BTC")
                .type(TransactionSimplified.Type.BUY)
                .amount(BigDecimal.valueOf(2))
                .dollarValue(BigDecimal.valueOf(120))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 2).atStartOfDay())
                .build();
        var tx3 = TransactionSimplified.builder()
                .type(TransactionSimplified.Type.SELL)
                .token("BTC")
                .amount(BigDecimal.valueOf(1.5))
                .dollarValue(BigDecimal.valueOf(75))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 3).atStartOfDay())
                .build();

        var txs = List.of(tx1, tx2, tx3);

        var compute = new Compute(txs);
        var pnls = compute.execute("fifo");

        assertEquals(BigDecimal.ZERO, pnls.get(0).getValue());
        assertEquals(BigDecimal.ZERO, pnls.get(1).getValue());
        assertEquals(0, BigDecimal.valueOf(15).compareTo(pnls.get(2).getValue()));

    }

    @Test
    void TestComputeAverage() {
        var tx1 = TransactionSimplified.builder()
                .type(TransactionSimplified.Type.BUY)
                .token("BTC")
                .amount(BigDecimal.valueOf(1))
                .dollarValue(BigDecimal.valueOf(30))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 1).atStartOfDay())
                .build();
        var tx2 = TransactionSimplified.builder()
                .token("BTC")
                .type(TransactionSimplified.Type.BUY)
                .amount(BigDecimal.valueOf(2))
                .dollarValue(BigDecimal.valueOf(120))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 2).atStartOfDay())
                .build();
        var tx3 = TransactionSimplified.builder()
                .type(TransactionSimplified.Type.SELL)
                .token("BTC")
                .amount(BigDecimal.valueOf(1.5))
                .dollarValue(BigDecimal.valueOf(75))
                .localDateTime(LocalDate.of(2024, Month.JANUARY, 3).atStartOfDay())
                .build();

        var txs = List.of(tx1, tx2, tx3);

        var compute = new Compute(txs);
        var pnls = compute.execute("average");

        // Check that buy transactions result in zero PnL
        assertEquals(BigDecimal.ZERO, pnls.get(0).getValue());
        assertEquals(BigDecimal.ZERO, pnls.get(1).getValue());

        // Check that the calculated PnL matches the expected PnL
        assertEquals(0, BigDecimal.ZERO.compareTo(pnls.get(2).getValue()));
    }
}