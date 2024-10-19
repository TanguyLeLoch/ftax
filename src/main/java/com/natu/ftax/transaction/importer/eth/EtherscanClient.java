package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.common.exception.FunctionalException;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Component
public class EtherscanClient {

    @Value("${etherscan.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final Bucket perSecondBucket;
    private final Bucket perDayBucket;

    public EtherscanClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // Per-Second Limit Bucket (blocks when limit is reached)
        perSecondBucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(5).refillGreedy(1, Duration.ofMillis(200)))
                .build();
        Instant firstRefillTime = LocalDateTime.now(ZoneId.of("UTC"))
                .truncatedTo(ChronoUnit.DAYS).plusDays(1)
                .toInstant(ZoneOffset.UTC);
        perDayBucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(100_000).
                        refillIntervallyAligned(100_000, Duration.ofHours(24), firstRefillTime))
                .build();
    }

    public <T> ResponseEntity<T> getForEntity(URI uri, ParameterizedTypeReference<T> responseType) {
        // Consume from per-day bucket (throws exception if limit reached)
        if (!perDayBucket.tryConsume(1)) {
            throw new FunctionalException("Daily etherscan API rate limit exceeded");
        }

        // Consume from per-second bucket (blocks if limit reached)
        try {
            perSecondBucket.asBlocking().consume(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while waiting for rate limiter", e);
        }

        // Proceed with the API call
        return restTemplate.exchange(uri, HttpMethod.GET, null, responseType);

    }

    public URI buildUri(String module, String action, UriComponentsBuilder builder) {
        return builder
                .queryParam("module", module)
                .queryParam("action", action)
                .queryParam("apikey", apiKey)
                .build()
                .encode()
                .toUri();
    }


}
