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
