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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Component("EthereumImporter")
public class EthereumImporter implements OnChainImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EthereumImporter.class);

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
        var totalTx = ethTxes.size();
        var count = 0;
        // Process transactions as needed
        txLoop:
        for (EtherscanApi.EthTx tx : ethTxes) {
            count++;
            System.out.println("Processing tx: " + count + "/" + totalTx);
            var hash = tx.hash();
            var ldt = convertToLocalDateTime(tx.timeStamp());

            if (!"0".equals(tx.value())) {

                var amount = parseWei(tx.value());
                var side = tx.from().equals(address) ? Transaction.Type.SELL : Transaction.Type.BUY;
                var ethToken = tokenRepo.findByTicker("ETH").orElseThrow(() -> new FunctionalException("Cant find Eth Token"));

                BigDecimal price = etherscanApi.getEtherPriceAt(tx.blockNumber());
                //create eth tx
                var ethTx = Transaction.builder()
                        .id(idGenerator.generate())
                        .client(client)
                        .amount(amount)
                        .type(side)
                        .localDateTime(ldt)
                        .price(price)
                        .token(ethToken.getId())
                        .externalId(hash)
                        .build();

                txToSave.add(ethTx);
            }
            List<EventLog> logs = etherscanApi.getLogsforTx(tx, address);


            for (var log : logs) {

                var contractAddress = log.address().toLowerCase();
                Optional<Token> tokenOpt = tokenRepo.findByExternalId(contractAddress);
                Token token;

                if (tokenOpt.isPresent()) {
                    token = tokenOpt.get();
                } else {
                    String tokenId = idGenerator.generate();
                    var info = dextoolApi.getTokenInfo(contractAddress);

                    if (info == null) {
                        Transaction emptyTx = Transaction.builder()
                                .id(idGenerator.generate())
                                .client(client)
                                .localDateTime(ldt)
                                .type(Transaction.Type.BUY)
                                .token(contractAddress)
                                .externalId(hash)
                                .build();
                        txToSave.add(emptyTx);
                        LOGGER.warn("Token not found in dextool: " + contractAddress);
                        continue txLoop;
                    }

                    Token t = new Token(tokenId, info.symbol(), info.name(), contractAddress, info.decimals());
                    token = tokenRepo.save(t);
                }

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
                        .type(side)
                        .amount(amount)
                        .localDateTime(ldt)
                        .externalId(hash)
                        .build();

                txToSave.add(txFromLog);
            }


            matchPrices(txToSave, hash);


        }

        transactionRepo.saveAll(txToSave);

    }

    private static void matchPrices(List<Transaction> txToSave, String hash) {
        List<Transaction> txForHash = txToSave.stream().filter(t -> t.getExternalId().equals(hash)).toList();

        if (txForHash.size() != 2) {
            return;
        }
        boolean hasBuy = txForHash.stream().anyMatch(t -> t.getType() == Transaction.Type.BUY);
        boolean hasSell = txForHash.stream().anyMatch(t -> t.getType() == Transaction.Type.SELL);
        if (!hasBuy || !hasSell) {
            return;
        }
        var nullPrice = txForHash.stream().filter(t -> t.getPrice() == null).findFirst();
        var nonNullPrice = txForHash.stream().filter(t -> t.getPrice() != null).findFirst();
        if (nullPrice.isEmpty() || nonNullPrice.isEmpty()) {
            return;
        }

        // both price should be the same
        BigDecimal totalCost = nonNullPrice.get().getPrice().multiply(nonNullPrice.get().getAmount());
        nullPrice.get().setPrice(totalCost.divide(nullPrice.get().getAmount(), MathContext.DECIMAL64));
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