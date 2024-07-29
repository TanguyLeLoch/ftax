#!/bin/bash

# Function to convert SequentialIdGenerator.kt to SequentialIdGenerator.java
convert_sequential_id_generator() {
  cat <<EOL > SequentialIdGenerator.java
package com.natu.ftax.IDgenerator.infrastructure;

import com.natu.ftax.IDgenerator.domain.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

public class SequentialIdGenerator implements IdGenerator {
    private final AtomicLong counter = new AtomicLong(0);

    @Override
    public String generate() {
        return Long.toString(counter.incrementAndGet());
    }
}
EOL
}

# Function to convert SnowflakeIDGeneratorTest.kt to SnowflakeIDGeneratorTest.java
convert_snowflake_id_generator_test() {
  cat <<EOL > SnowflakeIDGeneratorTest.java
package com.natu.ftax.IDgenerator.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class SnowflakeIDGeneratorTest {

    @Test
    void testConcurrentIDGeneration() throws InterruptedException {
        int threadCount = 100;
        SnowflakeIDGenerator idGenerator = new SnowflakeIDGenerator(1, Clock.systemUTC());
        Set<String> ids = Collections.synchronizedSet(new HashSet<>());
        var executor = Executors.newCachedThreadPool();
        var latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                    String id = idGenerator.generate();
                    synchronized (ids) {
                        Assertions.assertFalse(ids.contains(id), "ID should be unique: " + id);
                        ids.add(id);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
        }
        executor.shutdown();
        boolean finished = executor.awaitTermination(1, TimeUnit.SECONDS);
        Assertions.assertTrue(finished, "Not all tasks completed in the given time frame");
    }

    @Test
    void testIDFormat() {
        SnowflakeIDGenerator idGenerator = new SnowflakeIDGenerator(1, Clock.systemUTC());
        String id = idGenerator.generate();
        org.assertj.core.api.Assertions.assertThat(id).matches("\\d+-\\d+-\\d+");
    }

    @Test
    void testClockMovedBackwards() {
        Clock clock = Mockito.mock(Clock.class);
        long currentTime = Clock.systemUTC().millis();
        Mockito.when(clock.millis()).thenReturn(currentTime).thenReturn(currentTime - 1L).thenReturn(currentTime);
        SnowflakeIDGenerator idGenerator = new SnowflakeIDGenerator(1, clock);
        String id1 = idGenerator.generate();
        String id2 = idGenerator.generate();
        org.assertj.core.api.Assertions.assertThat(id1.split("-")[1]).isEqualTo("0");
        org.assertj.core.api.Assertions.assertThat(id2.split("-")[1]).isEqualTo("1");
        Mockito.verify(clock, Mockito.times(3)).millis();
    }
}
EOL
}

# Function to convert LedgerEntryTest.kt to LedgerEntryTest.java
convert_ledger_entry_test() {
  cat <<EOL > LedgerEntryTest.java
package com.natu.ftax.ledger.domain;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.IDgenerator.infrastructure.SequentialIdGenerator;
import com.natu.ftax.transaction.TransactionTestUtils;
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
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "token1", "token2", "token3");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator);

        Assertions.assertThat(ledgerEntry.getId()).isEqualTo("second");
        Assertions.assertThat(ledgerEntry.getBalances().size()).isEqualTo(3);
        Assertions.assertThat(ledgerEntry.getBalances().get(tx1.getTokenIn()).getAmount()).isEqualTo(1.0);
        Assertions.assertThat(ledgerEntry.getBalances().get(tx1.getTokenOut()).getAmount()).isEqualTo(-5.0);
        Assertions.assertThat(ledgerEntry.getBalances().get(tx1.getTokenFee()).getAmount()).isEqualTo(0.1);
    }

    @Test
    void testGenerateLedgerBookWithRealistSwap() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator);

        Assertions.assertThat(ledgerEntry.getId()).isEqualTo("second");
        Assertions.assertThat(ledgerEntry.getBalances().size()).isEqualTo(2);
        Assertions.assertThat(ledgerEntry.getBalances().get("BTC").getAmount()).isEqualTo(1.1);
        Assertions.assertThat(ledgerEntry.getBalances().get("ETH").getAmount()).isEqualTo(-5.0);
    }

    @Test
    void testGenerateLedgerBookWithRealistSwap2() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator);
        Transaction tx2 = TransactionTestUtils.submittedTransactionWithValue("tx2", 1.0, 5.0, 0.1, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2, idGenerator);

        Assertions.assertThat(ledgerEntry2.getId()).isEqualTo("third");
        Assertions.assertThat(ledgerEntry2.getBalances().size()).isEqualTo(2);
        Assertions.assertThat(ledgerEntry2.getBalances().get("BTC").getAmount()).isEqualTo(2.2);
        Assertions.assertThat(ledgerEntry2.getBalances().get("ETH").getAmount()).isEqualTo(-10.0);
    }

    @Test
    void shouldNotContainTokensWithZeroBalance() {
        LedgerEntry firstLedgerEntry = LedgerEntry.first("first");
        Transaction tx1 = TransactionTestUtils.submittedTransactionWithValue("tx1", 1.0, 5.0, 0.0, "BTC", "ETH", "BTC");
        LedgerEntry ledgerEntry = LedgerEntry.create("second", firstLedgerEntry, tx1, idGenerator);
        Transaction tx2 = TransactionTestUtils.submittedTransactionWithValue("tx2", 5.0, 1.0, 0.0, "ETH", "BTC", "BTC");
        LedgerEntry ledgerEntry2 = LedgerEntry.create("third", ledgerEntry, tx2, idGenerator);

        ledgerEntry2.getBalances().forEach((key, value) -> {
            System.out.println(key + " " + value.getAmount() + " " + value.getToken());
        });

        Assertions.assertThat(ledgerEntry2.getBalances().size()).isEqualTo(0);
    }
}
EOL
}

# Function to convert LedgerBookTest.kt to LedgerBookTest.java
convert_ledger_book_test() {
  cat <<EOL > LedgerBookTest.java
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
EOL
}

# Function to convert FtaxApplicationTests.kt to FtaxApplicationTests.java
convert_ftax_application_tests() {
  cat <<EOL > FtaxApplicationTests.java
package com.natu.ftax;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FtaxApplicationTests {

    @Test
    void contextLoads() {
    }
}
EOL
}

# Call the conversion functions
convert_sequential_id_generator
convert_snowflake_id_generator_test
convert_ledger_entry_test
convert_ledger_book_test
convert_ftax_application_tests
