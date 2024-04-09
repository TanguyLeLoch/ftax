package com.natu.ftax.IDgenerator.infrastructure

import com.natu.ftax.IDgenerator.domain.IdGenerator
import java.util.concurrent.atomic.AtomicLong

class SequentialIdGenerator : IdGenerator {
    private val counter = AtomicLong(0)

    override fun generate(): String {
        return counter.incrementAndGet().toString()
    }
}