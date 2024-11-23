package com.natu.ftax.transaction.importer.eth;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.common.exception.TechnicalException;
import io.github.bucket4j.Bucket;

@Component
public class EtherscanClient {

    @Value("${etherscan.api.key}")
    private String apiKey;
    private final Logger LOGGER = LoggerFactory.getLogger(EtherscanClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Bucket perSecondBucket;
    private final Bucket perDayBucket;

    public EtherscanClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;

        // Per-Second Limit Bucket (blocks when limit is reached)
        perSecondBucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(3).refillGreedy(3, Duration.ofMillis(1000)))
                .build();
        Instant firstRefillTime = LocalDateTime.now(ZoneId.of("UTC"))
                .truncatedTo(ChronoUnit.DAYS).plusDays(1)
                .toInstant(ZoneOffset.UTC);
        perDayBucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(100_000).
                        refillIntervallyAligned(100_000, Duration.ofHours(24), firstRefillTime))
                .build();
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> getForEntity(URI uri, TypeReference<T> typeRef) {

        consumeTokens();
        // Proceed with the API call
        ParameterizedTypeReference<EtherscanApi.Response<?>> responseType = new ParameterizedTypeReference<>() {
        };
        try {
            ResponseEntity<EtherscanApi.Response<?>> resp = restTemplate.exchange(uri, HttpMethod.GET, null, responseType);
            if (resp.getBody() != null && resp.getBody().isSuccess()) {
                Object result = resp.getBody().result();
                if (typeRef.getType().equals(String.class) && result instanceof String s) {
                    return Optional.of((T) s);
                }

                // Convert the result object to JSON string first, then to target type
                String jsonString = objectMapper.writeValueAsString(result);
                T parsedResult = objectMapper.readValue(jsonString, typeRef);
                return Optional.of(parsedResult);
            } else {
                String body = resp.getBody().toString();
                if (body.contains("No transactions found") || body.contains("No records found")) {
                    return Optional.empty();
                }
                LOGGER.error("Error code: {}, message: {}", resp.getStatusCode(), body);
                throw new EthereumImporter.TxInterruptionNeeded();
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("Client error when calling Etherscan API at URI: {}", uri, e);
            LOGGER.error("Error code: {}, message: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new EthereumImporter.TxInterruptionNeeded();
        } catch (RestClientException e) {
            LOGGER.error("Client error when calling Etherscan API at URI: {}", uri, e);
            LOGGER.error("Error message: {}", e.getMessage());
            throw new EthereumImporter.TxInterruptionNeeded();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error when parsing JSON response", e);
            throw new EthereumImporter.TxInterruptionNeeded();
        }
    }

    private synchronized void consumeTokens() {
        // Consume from per-day bucket (throws exception if limit reached)
        if (!perDayBucket.tryConsume(1)) {
            throw new FunctionalException("Daily etherscan API rate limit exceeded");
        }

        // Consume from per-second bucket (blocks if limit reached)
        try {
            perSecondBucket.asBlocking().consume(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TechnicalException("Thread was interrupted while waiting for rate limiter", e);
        }
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
