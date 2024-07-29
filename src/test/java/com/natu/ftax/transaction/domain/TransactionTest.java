package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @Test
    void shouldCreateDraftTransaction() {
        Transaction draftTransaction = Transaction.create("123");

        assertEquals("123", draftTransaction.getId());
        assertEquals("draft", draftTransaction.getState());
    }

    @Test
    void shouldCreateTransactionFromDraftTransaction() {
        Transaction transaction = Transaction.create("abc");

        transaction.submit(
                new SubmitTransactionCommand(
                        "abc",
                        "swap",
                        Instant.now(),
                        "0x123",
                        "0x456",
                        "0x789",
                        new BigDecimal("120.0"),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "0x124"
                )
        );

        assertEquals("abc", transaction.getId());
        assertEquals("submitted", transaction.getState());
        assertEquals("swap", transaction.getTransactionType());
        assertEquals("0x123", transaction.getTokenIn().getSymbol());
        assertEquals("0x456", transaction.getTokenOut().getSymbol());
        assertEquals("0x789", transaction.getTokenFee().getSymbol());
        assertEquals(new BigDecimal("120.0"), transaction.getAmountIn());
        assertEquals(BigDecimal.ZERO, transaction.getAmountOut());
        assertEquals(BigDecimal.ZERO, transaction.getAmountFee());
        assertEquals("0x124", transaction.getExternalId());
    }

    @ParameterizedTest
    @MethodSource("amountArguments")
    void shouldNotBeAbleToSubmitTransactionWithNegativeAmounts(
            BigDecimal amountIn,
            BigDecimal amountOut, BigDecimal amountFee) {
        Transaction tx = Transaction.create("anId");

        Executable executable = () -> tx.submit(
                new SubmitTransactionCommand(
                        "anId",
                        "swap",
                        Instant.now(),
                        "0x123",
                        "0x456",
                        "0x789",
                        amountIn,
                        amountOut,
                        amountFee,
                        "0x123"
                )
        );

        FunctionalException exception = assertThrows(FunctionalException.class, executable);
        Assertions.assertThat(exception.getMessage()).contains("Value cannot be negative");
    }

    @Test
    void shouldBeAbleToEditTransaction() {
        Transaction transaction = submittedTransaction();

        transaction.edit();

        assertEquals("abc", transaction.getId());
        assertEquals("draft", transaction.getState());
    }

    @Test
    void shouldNotBeAbleToEditDraftTransaction() {
        Transaction transaction = Transaction.create("abc");

        Executable executable = transaction::edit;
        FunctionalException exception = assertThrows(FunctionalException.class, executable);

        Assertions.assertThat(exception.getMessage()).contains("Transaction is not in SUBMITTED state");
    }

    private Transaction submittedTransaction() {
        Transaction transaction = Transaction.create("abc");

        transaction.submit(
                new SubmitTransactionCommand(
                        "abc",
                        "swap",
                        Instant.now(),
                        "0x123",
                        "0x456",
                        "0x789",
                        BigDecimal.valueOf(120.0),
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        "0x124"
                )
        );
        return transaction;
    }

    private static Stream<Arguments> amountArguments() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1.0), BigDecimal.ZERO, BigDecimal.ZERO),
                Arguments.of(BigDecimal.ZERO, BigDecimal.valueOf(-1.0), BigDecimal.ZERO),
                Arguments.of(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(-1.0))
        );
    }
}