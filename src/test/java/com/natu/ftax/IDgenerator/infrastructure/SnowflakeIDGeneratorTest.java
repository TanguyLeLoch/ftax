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
