package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.transaction.importer.eth.model.EventLog;
import com.natu.ftax.transaction.importer.eth.model.LogsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Collections.emptyList;

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

    public List<EthTx> getTransactions(String address, long startBlock,
                                       long endBlock, int page, int offset, String sort) {
        String url = baseUrl + "?module=account&action=txlist"
                + "&address=" + address
                + "&startblock=" + startBlock
                + "&endblock=" + endBlock
                + "&page=" + page
                + "&offset=" + offset
                + "&sort=" + sort
                + "&apikey=" + apiKey;

        ResponseEntity<TxListResponse> response = restTemplate.getForEntity(
                url, TxListResponse.class);

        if (response.getBody() != null && "1".equals(
                response.getBody().status())) {
            return response.getBody().result();
        }

        // Handle error case as needed
        return emptyList();
    }

    public List<EventLog> getLogsforTx(EthTx tx, String address) {

        String topic0 = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";

        String url = baseUrl + "?module=logs&action=getLogs"
                + "&fromBlock=" + tx.blockNumber()
                + "&toBlock=" + tx.blockNumber()
                + "&topic0=" + topic0
                + "&page=1"
                + "&offset=1000"
                + "&apikey=" + apiKey;

        ResponseEntity<LogsResponse> response = restTemplate.getForEntity(url, LogsResponse.class);

        LogsResponse logsResponse = response.getBody();

        if (logsResponse == null || !"1".equals(logsResponse.status())) {
            return emptyList();
        }
        String addr = address.substring(2);
        return logsResponse.result().stream()
                .filter(log -> log.transactionHash().equals(tx.hash()))
                .filter(log -> isAddressInTopics(log, addr))
                .toList();
    }

    private static boolean isAddressInTopics(EventLog log, String addr) {
        return log.topics().stream()
                .anyMatch(topic -> topic.contains(addr));
    }

    public record BlockNumberResponse(String status, String message, String result) {
    }

    public record TxListResponse(String status, String message, List<EthTx> result) {
    }

    public record EthTx(
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

