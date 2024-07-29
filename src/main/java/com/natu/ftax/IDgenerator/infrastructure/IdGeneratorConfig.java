package com.natu.ftax.IDgenerator.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class IdGeneratorConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public SnowflakeIDGenerator idGenerator(@Value("${instance.id}") int instanceId, Clock clock) {
        return new SnowflakeIDGenerator(instanceId, clock);
    }
}