package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.transaction.importer.eth.model.EventLog;
import com.natu.ftax.transaction.importer.eth.model.LogsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
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

    /**
     * Improvement: we should prorate the price depending on whether the event is a swapIn or swapOut
     */
    public BigDecimal getEtherPriceAt(String blockNumber) {
        String usdtWethPair = "0x0d4a11d5EEaaC28EC3F61d100daF4d40471f1852";
        String swapTopic = "0xd78ad95fa46c994b6551d0da85fc275fe613ce37657fb8d5e3d130840159d822";

        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("module", "logs")
                .queryParam("action", "getLogs")
                .queryParam("address", usdtWethPair)
                .queryParam("topic0", swapTopic)
                .queryParam("fromBlock", blockNumber)
                .queryParam("page", "1")
                .queryParam("offset", "20")
                .queryParam("apikey", apiKey)
                .build()
                .encode()
                .toUri();


        ResponseEntity<LogsResponse> response = restTemplate.getForEntity(
                uri, LogsResponse.class);

        if (response.getBody() == null || !"1".equals(response.getBody().status())) {
            return null;
        }

        // average price
        List<EventLog> logs = response.getBody().result();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int priceCount = 0;

        for (EventLog log : logs) {
            BigDecimal priceFromLog = convertSwapDataToEthPrice(log);
            if (priceFromLog != null) {
                totalPrice = totalPrice.add(priceFromLog);
                priceCount++;
            }
        }

        if (priceCount == 0) {
            return null;
        }

        return totalPrice.divide(BigDecimal.valueOf(priceCount), MathContext.DECIMAL64);
    }

    BigDecimal convertSwapDataToEthPrice(EventLog log) {
        var data = log.data();
        // Decimals for USDT and WETH
        BigDecimal usdtDecimals = new BigDecimal("1000000"); // USDT has 6 decimals
        BigDecimal wethDecimals = new BigDecimal("1000000000000000000"); // WETH has 18 decimals

        String dataHex = data.startsWith("0x") ? data.substring(2) : data; // Remove "0x" if present

        // Each uint256 is 64 hex characters (32 bytes)
        if (dataHex.length() != 256) {
            return null;
        }

        String amount0InHex = dataHex.substring(0, 64);
        String amount1InHex = dataHex.substring(64, 128);
        String amount0OutHex = dataHex.substring(128, 192);
        String amount1OutHex = dataHex.substring(192, 256);

        BigInteger amount0In = new BigInteger(amount0InHex, 16);
        BigInteger amount1In = new BigInteger(amount1InHex, 16);
        BigInteger amount0Out = new BigInteger(amount0OutHex, 16);
        BigInteger amount1Out = new BigInteger(amount1OutHex, 16);

        BigDecimal usdtAmount;
        BigDecimal wethAmount;


        if (amount0In.compareTo(BigInteger.ZERO) > 0 && amount1Out.compareTo(BigInteger.ZERO) > 0) {
            // WETH in, USDT out
            usdtAmount = new BigDecimal(amount1Out).divide(usdtDecimals, MathContext.DECIMAL64);
            wethAmount = new BigDecimal(amount0In).divide(wethDecimals, MathContext.DECIMAL64);
        } else if (amount1In.compareTo(BigInteger.ZERO) > 0 && amount0Out.compareTo(BigInteger.ZERO) > 0) {
            // USDT in, WETH out
            usdtAmount = new BigDecimal(amount1In).divide(usdtDecimals, MathContext.DECIMAL64);
            wethAmount = new BigDecimal(amount0Out).divide(wethDecimals, MathContext.DECIMAL64);
        } else {
            // No valid amounts, skip
            return null;
        }

        // Compute price
        if (wethAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("WETH amount is zero in event");
            return null;
        }

        return usdtAmount.divide(wethAmount, MathContext.DECIMAL64);
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

