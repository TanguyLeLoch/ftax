package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.transaction.importer.eth.model.EventLog;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class EtherscanApi {

    public static final String BASE_URL = "https://api.etherscan.io/api";
    public static final String WITHDRAW_TOPIC = "0x7fcf532c15f0a6db0bd6d0e038bea71d30d808c7d98cb3bf7268a95bf5081b65";
    public static final String TRANSFER_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    private final EtherscanClient etherscanClient;

    public EtherscanApi(EtherscanClient etherscanClient) {
        this.etherscanClient = etherscanClient;
    }

    public Integer getBlockNumberByTimestamp(long timestamp, String closest) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("timestamp", timestamp)
                .queryParam("closest", closest);

        URI uri = etherscanClient.buildUri("block", "getblocknobytime", builder);

        ParameterizedTypeReference<Response<String>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<Response<String>> response = etherscanClient.getForEntity(uri, responseType);

        if (response.getBody() != null && "1".equals(response.getBody().status())) {
            return Integer.parseInt(response.getBody().result());
        }

        // Handle error case as needed
        return null;
    }

    public List<EthTx> getTransactions(String address, long startBlock,
                                       long endBlock, int page, int offset, String sort) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("address", address)
                .queryParam("startblock", startBlock)
                .queryParam("endblock", endBlock)
                .queryParam("page", page)
                .queryParam("offset", offset)
                .queryParam("sort", sort);

        URI uri = etherscanClient.buildUri("account", "txlist", builder);

        ParameterizedTypeReference<Response<List<EthTx>>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<Response<List<EthTx>>> response = etherscanClient.getForEntity(uri, responseType);

        if (response.getBody() != null && "1".equals(response.getBody().status())) {
            return response.getBody().result();
        }

        return emptyList();
    }

    public List<InternalTx> getInternalTxs(EthTx tx, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("txHash", tx.hash());

        URI uri = etherscanClient.buildUri("account", "txlistinternal", builder);

        ParameterizedTypeReference<Response<List<InternalTx>>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<Response<List<InternalTx>>> response = etherscanClient.getForEntity(uri, responseType);

        if (response.getBody() != null && "1".equals(response.getBody().status())) {
            List<InternalTx> txs = response.getBody().result();
            return txs.stream()
                    .filter(itx -> itx.from().equals(address) || itx.to().equals(address)).toList();
        }

        // Handle error case as needed
        return List.of();
    }

    public List<EventLog> getLogsforTx(EthTx tx, String address) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("fromBlock", tx.blockNumber())
                .queryParam("toBlock", tx.blockNumber())
                .queryParam("topic0", TRANSFER_TOPIC)
                .queryParam("topic0_1_opr", "or")
                .queryParam("topic1", WITHDRAW_TOPIC)
                .queryParam("page", "1")
                .queryParam("offset", "1000");

        Response<List<EventLog>> logsResponse = getLogsResponse(builder);

        if (logsResponse == null || !logsResponse.isSuccess()) {
            return emptyList();
        }

        String addr = address.substring(2);
        return logsResponse.result().stream()
                .filter(log -> log.transactionHash().equals(tx.hash()))
                .filter(log -> isAddressInTransferTopics(log, addr))
                .toList();
    }

    private Response<List<EventLog>> getLogsResponse(UriComponentsBuilder builder) {
        URI uri = etherscanClient.buildUri("logs", "getLogs", builder);
        ParameterizedTypeReference<Response<List<EventLog>>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<Response<List<EventLog>>> response = etherscanClient.getForEntity(uri, responseType);

        return response.getBody();
    }

    private static boolean isAddressInTransferTopics(EventLog log, String addr) {
        return TRANSFER_TOPIC.equals(log.topics().get(0))
                && log.topics().stream().anyMatch(topic -> topic.contains(addr));
    }

    /**
     * Improvement: we should prorate the price depending on whether the event is a swapIn or swapOut
     */
    public BigDecimal getEtherPriceAt(String blockNumber) {
        String usdtWethPair = "0x0d4a11d5EEaaC28EC3F61d100daF4d40471f1852";
        String swapTopic = "0xd78ad95fa46c994b6551d0da85fc275fe613ce37657fb8d5e3d130840159d822";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("address", usdtWethPair)
                .queryParam("topic0", swapTopic)
                .queryParam("fromBlock", blockNumber)
                .queryParam("page", "1")
                .queryParam("offset", "20");

        var logsResponse = getLogsResponse(builder);

        if (logsResponse == null || !logsResponse.isSuccess()) {
            return null;
        }

        // average price
        List<EventLog> logs = logsResponse.result();
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


    public record Response<T>(String status, String message, T result) {

        boolean isSuccess() {
            return status.equals("1");
        }

    }

    public record InternalTx(
            String blockNumber,
            String timeStamp,
            String from,
            String to,
            String value,
            String contractAddress,
            String input,
            String type,
            String gas,
            String gasUsed,
            String isError,
            String errCode) {
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
