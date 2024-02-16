package com.natu.ftax.IDgenerator.infrastructure


import com.natu.ftax.IDgenerator.domain.IdGenerator
import org.slf4j.LoggerFactory
import java.time.Clock

class SnowflakeIDGenerator(private val instanceId: Int, private val clock: Clock) : IdGenerator {

    companion object {
        private val lock = Any()
        private var lastTimestamp = -1L
        private var sequence = 0
        private val LOGGER = LoggerFactory.getLogger(SnowflakeIDGenerator::class.java)
    }

    override fun generate(): String {
        var id = tryGenerateId()
        while (id == null) {
            id = tryGenerateId()
        }
        return id
    }

    private fun tryGenerateId(): String? {
        synchronized(lock) {
            val timestamp = clock.millis()
            if (timestamp < lastTimestamp) {
                LOGGER.warn(
                    "Clock moved backwards. Refusing to generate id for {} milliseconds.", (lastTimestamp - timestamp)
                )
                try {
                    Thread.sleep(lastTimestamp - timestamp)
                } catch (ignored: InterruptedException) {
                }
                return null
            } else if (timestamp == lastTimestamp) {
                // Increment the sequence number if the timestamp is the same
                sequence++
            } else {
                // Reset the sequence for a new timestamp
                sequence = 0
                lastTimestamp = timestamp
            }
            return "$timestamp-$sequence-$instanceId"
        }
    }


}