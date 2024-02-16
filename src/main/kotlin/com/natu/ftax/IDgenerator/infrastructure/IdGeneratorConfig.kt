package com.natu.ftax.IDgenerator.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class IdGeneratorConfig {

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }

    @Bean
    fun idGenerator(@Value("\${instance.id}") instanceId: Int, clock: Clock): SnowflakeIDGenerator =
        SnowflakeIDGenerator(instanceId, clock)

}