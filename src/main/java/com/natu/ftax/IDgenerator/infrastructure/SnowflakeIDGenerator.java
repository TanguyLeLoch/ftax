package com.natu.ftax.IDgenerator.infrastructure;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class SnowflakeIDGenerator implements IdGenerator {

    private final int instanceId;
    private final Clock clock;

    private static final Object lock = new Object();
    private static long lastTimestamp = -1L;
    private static int sequence = 0;
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIDGenerator.class);

    public SnowflakeIDGenerator(int instanceId, Clock clock) {
        this.instanceId = instanceId;
        this.clock = clock;
    }

    @Override
    public String generate() {
        String id = tryGenerateId();
        while (id == null) {
            id = tryGenerateId();
        }
        return id;
    }

    private String tryGenerateId() {
        synchronized (lock) {
            long timestamp = clock.millis();
            if (timestamp < lastTimestamp) {
                LOGGER.warn("Clock moved backwards. Refusing to generate id for {} milliseconds.", (lastTimestamp - timestamp));
                try {
                    Thread.sleep(lastTimestamp - timestamp);
                } catch (InterruptedException ignored) {
                }
                return null;
            } else if (timestamp == lastTimestamp) {
                sequence++;
            } else {
                sequence = 0;
                lastTimestamp = timestamp;
            }
            return timestamp + "-" + sequence + "-" + instanceId;
        }
    }
}