package com.natu.ftax.transaction.importer.eth;

import static java.util.Collections.emptyList;
import static com.natu.ftax.transaction.importer.eth.EthereumImporter.WETH;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.natu.ftax.token.Token;
import com.natu.ftax.transaction.importer.eth.model.EventLog;

@Component
public class EtherscanApi {

    public static final String BASE_URL = "https://api.etherscan.io/api";
    public static final String WITHDRAW_TOPIC = "0x7fcf532c15f0a6db0bd6d0e038bea71d30d808c7d98cb3bf7268a95bf5081b65";
    public static final String TRANSFER_TOPIC = "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";
    private static final String SWAP_TOPIC = "0xd78ad95fa46c994b6551d0da85fc275fe613ce37657fb8d5e3d130840159d822";
    private final EtherscanClient etherscanClient;

    public EtherscanApi(EtherscanClient etherscanClient) {
        this.etherscanClient = etherscanClient;
    }

    public Integer getBlockNumberByTimestamp(long timestamp, String closest) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("timestamp", timestamp)
                .queryParam("closest", closest);

        URI uri = etherscanClient.buildUri("block", "getblocknobytime", builder);

        TypeReference<String> typeRef = new TypeReference<>() {
        };

        var response = etherscanClient.getForEntity(uri, typeRef);

        return response.map(Integer::parseInt).orElse(null);
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
        TypeReference<List<EthTx>> typeRef = new TypeReference<>() {
        };

        Optional<List<EthTx>> response = etherscanClient.getForEntity(uri, typeRef);

        return response.orElse(emptyList());

    }

    public List<InternalTx> getInternalTxs(EthTx tx, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("txHash", tx.hash());

        URI uri = etherscanClient.buildUri("account", "txlistinternal", builder);
        TypeReference<List<InternalTx>> typeRef = new TypeReference<>() {
        };

        var response = etherscanClient.getForEntity(uri, typeRef);

        return response
                .map(internalTxes -> internalTxes.stream()
                        .filter(itx -> itx.from().equals(address) || itx.to().equals(address))
                        .toList())
                .orElseGet(List::of);
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

        List<EventLog> logsResponse = getLogsResponse(builder);


        String addr = address.substring(2);
        return logsResponse.stream()
                .filter(log -> log.transactionHash().equals(tx.hash()))
                .filter(log -> isAddressInTransferTopics(log, addr))
                .toList();
    }

    private List<EventLog> getLogsResponse(UriComponentsBuilder builder) {
        URI uri = etherscanClient.buildUri("logs", "getLogs", builder);

        TypeReference<List<EventLog>> typeRef = new TypeReference<>() {
        };

        Optional<List<EventLog>> response = etherscanClient.getForEntity(uri, typeRef);

        return response.orElse(emptyList());
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
        String usdtAddress = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("address", usdtWethPair)
                                           .queryParam("topic0", SWAP_TOPIC)
                .queryParam("fromBlock", blockNumber)
                .queryParam("page", "1")
                .queryParam("offset", "20");

        List<EventLog> logs = getLogsResponse(builder);

        // average price
        BigDecimal totalPrice = BigDecimal.ZERO;
        int priceCount = 0;

        for (EventLog log : logs) {
            BigDecimal priceFromLog = convertSwapDataToEthPrice(log, usdtAddress, 6);
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

    public BigDecimal getTokenPrice(Token token, String blockNumber, String wethPair) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                                           .queryParam("address", wethPair)
                                           .queryParam("topic0", SWAP_TOPIC)
                                           .queryParam("fromBlock", blockNumber)
                                           .queryParam("page", "1")
                                           .queryParam("offset", "5");

        List<EventLog> logs = getLogsResponse(builder);

        // average price
        BigDecimal totalPrice = BigDecimal.ZERO;
        int priceCount = 0;

        for (EventLog log : logs) {
            BigDecimal priceOfEthInTokenFromLog =
                convertSwapDataToEthPrice(log, token.getExternalId(), token.getDecimals());

            if (priceOfEthInTokenFromLog != null) {

                totalPrice = totalPrice.add(priceOfEthInTokenFromLog);
                priceCount++;
            }
        }

        if (priceCount == 0) {
            return null;
        }

        BigDecimal priceOfTokenInEth = totalPrice.divide(BigDecimal.valueOf(priceCount), MathContext.DECIMAL64);
        BigDecimal ethPrice = getEtherPriceAt(blockNumber);

        return ethPrice.divide(priceOfTokenInEth, MathContext.DECIMAL64);
    }

    private boolean shouldReverse(String externalId) {
        return externalId.compareTo(WETH) > 0;
    }

    BigDecimal convertSwapDataToEthPrice(EventLog log, String tokenAddress, int nbDecimalToken) {
        var data = log.data();
        // Decimals for USDT and WETH
        BigDecimal usdtDecimals = new BigDecimal(BigInteger.TEN.pow(nbDecimalToken));
        // USDT has 6 decimals
        BigDecimal wethDecimals = new BigDecimal(BigInteger.TEN.pow(18)); // WETH has 18 decimals

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

        if (lessThan2Is0(amount0In, amount1In, amount0Out, amount1Out)) {
            return null;
        }

        BigDecimal usdtAmount;
        BigDecimal wethAmount;
        if (!shouldReverse(tokenAddress)) {
            usdtAmount = new BigDecimal(amount0In.add(amount0Out));
            wethAmount = new BigDecimal(amount1In.add(amount1Out));
        } else {
            usdtAmount = new BigDecimal(amount1In.add(amount1Out));
            wethAmount = new BigDecimal(amount0In.add(amount0Out));
        }

        usdtAmount = usdtAmount.divide(usdtDecimals, MathContext.DECIMAL64);
        wethAmount = wethAmount.divide(wethDecimals, MathContext.DECIMAL64);

        // Compute price
        if (wethAmount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("WETH amount is zero in event");
            return null;
        }

        return usdtAmount.divide(wethAmount, MathContext.DECIMAL64);
    }

    private boolean lessThan2Is0(BigInteger... amounts) {
        return Stream.of(amounts)
                   .filter(amount -> amount != null && !amount.equals(BigInteger.ZERO))
                   .count() < 2;
    }

    public boolean isStatusOk(EthTx tx) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("txhash", tx.hash());

        URI uri = etherscanClient.buildUri("transaction", "getstatus", builder);

        TypeReference<TxStatus> typeRef = new TypeReference<>() {
        };

        Optional<TxStatus> response = etherscanClient.getForEntity(uri, typeRef);


        return response.map(resp -> "0".equals(resp.isError())).orElse(false);

    }

    public record TxStatus(
            String isError,
            String errDescription) {
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

    public record Response<T>(String status, String message, T result) {

        boolean isSuccess() {
            return status.equals("1");
        }

    }
}
