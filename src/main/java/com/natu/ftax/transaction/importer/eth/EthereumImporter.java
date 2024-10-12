package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.token.Token;
import com.natu.ftax.token.TokenRepo;
import com.natu.ftax.transaction.Transaction;
import com.natu.ftax.transaction.TransactionRepo;
import com.natu.ftax.transaction.importer.OnChainImporter;
import com.natu.ftax.transaction.importer.eth.model.EventLog;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component("EthereumImporter")
public class EthereumImporter implements OnChainImporter {

    private final EtherscanApi etherscanApi;
    private final IdGenerator idGenerator;
    private final DextoolApi dextoolApi;
    private final TokenRepo tokenRepo;
    private final TransactionRepo transactionRepo;

    public EthereumImporter(EtherscanApi etherscanApi,
                            IdGenerator idGenerator,
                            DextoolApi dextoolApi,
                            TokenRepo tokenRepo,
                            TransactionRepo transactionRepo
    ) {
        this.etherscanApi = etherscanApi;
        this.idGenerator = idGenerator;
        this.dextoolApi = dextoolApi;
        this.tokenRepo = tokenRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    public void importTransaction(String address, LocalDateTime from,
                                  LocalDateTime to, Client client) {
        long fromTimestamp = from.toEpochSecond(ZoneOffset.UTC);
        long toTimestamp = to.toEpochSecond(ZoneOffset.UTC);

        Integer startBlock = etherscanApi.getBlockNumberByTimestamp(
                fromTimestamp, "after");
        Integer endBlock = etherscanApi.getBlockNumberByTimestamp(toTimestamp,
                "before");

        if (startBlock == null || endBlock == null) {
            throw new FunctionalException(
                    "Invalid block range, failed to get block number from Etherscan API");
        }

        List<EtherscanApi.EthTx> ethTxes = etherscanApi.getTransactions(
                address,
                startBlock,
                endBlock,
                1,
                10000,
                "asc");

        List<Transaction> txToSave = new ArrayList<>();

        // Process transactions as needed
        for (EtherscanApi.EthTx tx : ethTxes) {
            var ldt = convertToLocalDateTime(tx.timeStamp());

            if (!"0".equals(tx.value())) {

                var amount = parseWei(tx.value());
                var side = tx.from().equals(address) ? Transaction.Type.SELL : Transaction.Type.BUY;
                var ethToken = tokenRepo.findByTicker("ETH").orElseThrow(() -> new FunctionalException("Cant find Eth Token"));
                //create eth tx
                var ethTx = Transaction.builder()
                        .id(idGenerator.generate())
                        .client(client)
                        .amount(amount)
                        .type(side)
                        .localDateTime(ldt)
                        .price(BigDecimal.valueOf(123))
                        .token(ethToken.getId())
                        .build();

                txToSave.add(ethTx);
            }
            List<EventLog> logs = etherscanApi.getLogsforTx(tx, address);

            for (var log : logs) {

                var contractAddress = log.address().toLowerCase();
                Token token = tokenRepo.findByExternalId(contractAddress)
                        .orElseGet(() -> {
                            String tokenId = idGenerator.generate();
                            var info = dextoolApi.getTokenInfo(contractAddress);

                            Token t = new Token(tokenId, info.symbol(), info.name(), contractAddress, info.decimals());
                            return tokenRepo.save(t);
                        });

                String addr = address.substring(2);
                int idx = IntStream.range(0, log.topics().size())
                        .filter(i -> log.topics().get(i).contains(addr))
                        .findFirst()
                        .orElse(-1);
                var side = idx == 2 ? Transaction.Type.BUY : Transaction.Type.SELL;
                var amount = getAmountFromData(log.data(), token.getDecimals());

                var txFromLog = Transaction.builder()
                        .id(idGenerator.generate())
                        .client(client)
                        .token(token.getId())
                        .price(BigDecimal.valueOf(0.003))
                        .type(side)
                        .amount(amount)
                        .localDateTime(ldt)
                        .build();

                txToSave.add(txFromLog);
            }

        }
        transactionRepo.saveAll(txToSave);

    }

    private BigDecimal parseWei(String weiStr) {
        BigDecimal wei = new BigDecimal(weiStr);
        return wei.divide(BigDecimal.TEN.pow(18), MathContext.DECIMAL64);
    }

    public static LocalDateTime convertToLocalDateTime(String timestampStr) {
        long epochSeconds = Long.parseLong(timestampStr);
        Instant instant = Instant.ofEpochSecond(epochSeconds);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static BigDecimal getAmountFromData(String hexValue, int decimals) {
        if (hexValue.startsWith("0x")) {
            hexValue = hexValue.substring(2);
        }

        BigInteger bigInt = new BigInteger(hexValue, 16);

        return new BigDecimal(bigInt).divide(BigDecimal.TEN.pow(decimals), MathContext.DECIMAL64);
    }
}