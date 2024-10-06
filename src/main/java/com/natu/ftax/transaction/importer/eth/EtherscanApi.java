package com.natu.ftax.transaction.importer.eth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class EtherscanApi {

    @Value("${etherscan.api.key}")
    private String apiKey;

    private final String baseUrl = "https://api.etherscan.io/api";
    private final RestTemplate restTemplate;

    public EtherscanApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Integer getBlockNumberByTimestamp(long timestamp, String closest) {
        String url = baseUrl + "?module=block&action=getblocknobytime"
            + "&timestamp=" + timestamp
            + "&closest=" + closest
            + "&apikey=" + apiKey;

        ResponseEntity<BlockNumberResponse> response = restTemplate.getForEntity(
            url, BlockNumberResponse.class);

        if (response.getBody() != null && "1".equals(
            response.getBody().status())) {
            return Integer.parseInt(response.getBody().result());
        }

        // Handle error case as needed
        return null;
    }

    public List<Transaction> getTransactions(String address, long startBlock,
        long endBlock, int page, int offset, String sort) {
        String url = baseUrl + "?module=account&action=txlist"
            + "&address=" + address
            + "&startblock=" + startBlock
            + "&endblock=" + endBlock
            + "&page=" + page
            + "&offset=" + offset
            + "&sort=" + sort
            + "&apikey=" + apiKey;

        ResponseEntity<TransactionListResponse> response = restTemplate.getForEntity(
            url, TransactionListResponse.class);

        if (response.getBody() != null && "1".equals(
            response.getBody().status())) {
            return response.getBody().result();
        }

        // Handle error case as needed
        return Collections.emptyList();
    }

    public record BlockNumberResponse(String status, String message,
                                      String result) {
    }

    public record TransactionListResponse(String status, String message,
                                          List<Transaction> result) {
    }

    public record Transaction(
        String blockNumber,
        String timeStamp,
        String hash,
        String from,
        String to,
        String value,
        String gasUsed,
        String gasPrice) {
    }
}

