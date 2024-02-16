package com.natu.ftax.IDgenerator.infrastructure

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Clock
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal class SnowflakeIDGeneratorTest {
    @Test
    @Throws(InterruptedException::class)
    fun testConcurrentIDGeneration() {
        val threadCount = 100
        val idGenerator = SnowflakeIDGenerator(1, Clock.systemUTC())
        val ids = Collections.synchronizedSet(HashSet<String>())
        val executor = Executors.newCachedThreadPool()
        val latch = CountDownLatch(threadCount)
        for (i in 0 until threadCount) {
            executor.submit {
                latch.countDown()
                try {
                    latch.await() // Ensures all threads start at the same time
                    val id: String = idGenerator.generate()
                    synchronized(ids) {
                        Assertions.assertFalse(ids.contains(id), "ID should be unique: $id")
                        ids.add(id)
                    }
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw RuntimeException(e)
                }
            }
        }
        executor.shutdown()
        val finished = executor.awaitTermination(1, TimeUnit.SECONDS)
        Assertions.assertTrue(finished, "Not all tasks completed in the given time frame")
    }

    @Test
    fun testIDFormat() {
        val idGenerator = SnowflakeIDGenerator(1, Clock.systemUTC())
        val id: String = idGenerator.generate()
        org.assertj.core.api.Assertions.assertThat(id).matches("\\d+-\\d+-\\d+")
    }

    @Test
    fun testClockMovedBackwards() {
        val clock = Mockito.mock(Clock::class.java)
        val currentTime = Clock.systemUTC().millis()
        Mockito.`when`(clock.millis()).thenReturn(currentTime).thenReturn(currentTime - 1L).thenReturn(currentTime)
        val idGenerator = SnowflakeIDGenerator(1, clock)
        val id: String = idGenerator.generate()
        val id2: String = idGenerator.generate()
        org.assertj.core.api.Assertions.assertThat(id.split("-".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1]).isEqualTo("0")
        org.assertj.core.api.Assertions.assertThat(id2.split("-".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1]).isEqualTo("1")
        Mockito.verify(clock, Mockito.times(3)).millis()
    }
}

