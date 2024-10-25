package com.natu.ftax.transaction.importer.eth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Component
public class DextoolApi {

    private static final String baseUrl = "https://www.dextools.io/shared/search/token";

    private final RestTemplate restTemplate;

    public DextoolApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    TokenInfo getTokenInfo(String contractAddress) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("query", contractAddress)
                .build()
                .encode()
                .toUri();


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Referer", "https://www.dextools.io/app/en/token/realtrumpcoins?t=" + Instant.now().toEpochMilli());
        httpHeaders.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                TokenResponse.class
        );

        if (response.getBody() == null) return null;

        return response.getBody().results.stream().findFirst().orElse(null);
    }


    public record TokenResponse(List<TokenInfo> results) {
    }

    public record TokenInfo(
            String symbol,
            String name,
            int decimals,
            String logo
    ) {
    }
}
