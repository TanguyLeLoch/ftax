package com.natu.ftax.transaction.importer.eth;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.token.Token;
import com.natu.ftax.token.TokenRepo;
import com.natu.ftax.transaction.Transaction;
import com.natu.ftax.transaction.TransactionRepo;
import com.natu.ftax.transaction.importer.MasterTxService;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.natu.ftax.transaction.importer.eth.EtherscanApi.TRANSFER_TOPIC;
import static com.natu.ftax.transaction.importer.eth.EtherscanApi.WITHDRAW_TOPIC;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component("EthereumImporter")
public class EthereumImporter implements OnChainImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EthereumImporter.class);

    private final EtherscanApi etherscanApi;
    private final IdGenerator idGenerator;
    private final DextoolApi dextoolApi;
    private final TokenRepo tokenRepo;
    private final TransactionRepo transactionRepo;
    private final MasterTxService masterTxService;

    public EthereumImporter(EtherscanApi etherscanApi,
                            IdGenerator idGenerator,
                            DextoolApi dextoolApi,
                            TokenRepo tokenRepo,
                            TransactionRepo transactionRepo,
                            MasterTxService masterTxService
    ) {
        this.etherscanApi = etherscanApi;
        this.idGenerator = idGenerator;
        this.dextoolApi = dextoolApi;
        this.tokenRepo = tokenRepo;
        this.transactionRepo = transactionRepo;
        this.masterTxService = masterTxService;
    }

    @Override
    public void importTransaction(String address, LocalDateTime from,
                                  LocalDateTime to, Client client) {
        List<EtherscanApi.EthTx> ethTxes = getEthTxes(address, from, to);

        List<Transaction> txToSave = ethTxes.parallelStream()
                .flatMap(tx -> process1tx(address, client, tx))
                .toList();

        transactionRepo.saveAll(txToSave);
    }

    private Stream<Transaction> process1tx(String address, Client client, EtherscanApi.EthTx tx) {
        try {
            return importOneTx(address, client, tx).stream();
        } catch (TxInterruptionNeeded e) {
            var hash = tx.hash();
            var ldt = convertToLocalDateTime(tx.timeStamp());
            Transaction emptyTx = Transaction.builder()
                    .platform("Ethereum")
                    .id(idGenerator.generate())
                    .client(client)
                    .localDateTime(ldt)
                    .type(Transaction.Type.BUY)
                    .externalId(hash)
                    .address(address)
                    .build();
            return Stream.of(emptyTx);
        }
    }

    private List<Transaction> importOneTx(String address, Client client, EtherscanApi.EthTx tx) {
        var hash = tx.hash();
        var ldt = convertToLocalDateTime(tx.timeStamp());

        List<Transaction> txs = new ArrayList<>();

        if (hasEthValue(tx)) {
            txs.add(createTxForEthValue(address, client, tx, ldt, hash));
        }

        List<EtherscanApi.InternalTx> internalTxs = etherscanApi.getInternalTxs(tx, address);
        if (!internalTxs.isEmpty()) {
            txs.addAll(createEthFromInternalTx(internalTxs, address, client, tx));
        }

        List<EventLog> logs = etherscanApi.getLogsforTx(tx, address);
        for (var log : logs) {

            if (iswithdrawal(log)) {
                txs.add(importTxFromWithdrawal(address, client, log, ldt, hash, tx));
            } else if (isTransfer(log)) {
                txs.add(importTxFromTransfer(address, client, log, ldt, hash));
            }
        }


        var matchedTxs = masterTxService.createMasterTransactions(txs, "Ethereum");

        matchPrices(matchedTxs, hash);
        return matchedTxs;

    }

    private List<Transaction> createEthFromInternalTx(List<EtherscanApi.InternalTx> internalTxs, String address, Client client, EtherscanApi.EthTx tx) {
        List<Transaction> txs = new ArrayList<>();
        for (EtherscanApi.InternalTx itx : internalTxs) {
            var side = itx.from().equals(address) ? Transaction.Type.SELL : Transaction.Type.BUY;
            txs.add(Transaction.builder()
                    .platform("Ethereum")
                    .id(idGenerator.generate())
                    .client(client)
                    .amount(parseWei(itx.value()))
                    .type(side)
                    .token(getEthToken().getId())
                    .localDateTime(convertToLocalDateTime(itx.timeStamp()))
                    .price(etherscanApi.getEtherPriceAt(tx.blockNumber()))
                    .externalId(tx.hash())
                    .address(address)
                    .build());

        }
        return txs;
    }


    private Transaction importTxFromWithdrawal(String address, Client client, EventLog log,
                                               LocalDateTime ldt, String hash, EtherscanApi.EthTx tx) {

        var amount = getAmountFromData(log.data(), 18);

        return Transaction.builder()
                .platform("Ethereum")
                .id(idGenerator.generate())
                .client(client)
                .token(getEthToken().getId())
                .type(Transaction.Type.BUY)
                .externalId(hash)
                .price(etherscanApi.getEtherPriceAt(tx.blockNumber()))
                .localDateTime(ldt)
                .amount(amount)
                .address(address)
                .build();
    }

    private Transaction importTxFromTransfer(String address, Client client, EventLog log, LocalDateTime ldt, String hash) {
        var contractAddress = log.address().toLowerCase();
        Token token = findOrCreateToken(contractAddress);

        String addr = address.substring(2);
        int idx = IntStream.range(0, log.topics().size())
                .filter(i -> log.topics().get(i).contains(addr))
                .findFirst()
                .orElse(-1);
        var side = idx == 2 ? Transaction.Type.BUY : Transaction.Type.SELL;
        var amount = getAmountFromData(log.data(), token.getDecimals());

        return Transaction.builder()
                .platform("Ethereum")
                .id(idGenerator.generate())
                .client(client)
                .token(token.getId())
                .type(side)
                .amount(amount)
                .localDateTime(ldt)
                .externalId(hash)
                .price(BigDecimal.valueOf(-1))
                .address(address)
                .build();


    }

    private Token findOrCreateToken(String contractAddress) {
        return tokenRepo.findByExternalId(contractAddress)
                .orElseGet(() -> createAndSaveToken(contractAddress));
    }

    private Token createAndSaveToken(String contractAddress) {
        String tokenId = idGenerator.generate();
        DextoolApi.TokenInfo info = dextoolApi.getTokenInfo(contractAddress);

        if (info == null) {
            LOGGER.warn("Token not found in dextool: " + contractAddress);
            throw new TxInterruptionNeeded();
        }
        String tokenUrl = isEmpty(info.logo()) ? "https://www.dextools.io/resources/tokens/logos/" + info.logo().split("\\?")[0] : null;
        Token token = new Token(tokenId, info.symbol(), info.name(), contractAddress, info.decimals(), tokenUrl);
        return tokenRepo.save(token);
    }


    private boolean isTransfer(EventLog log) {
        return log.topics().get(0).equals(TRANSFER_TOPIC);
    }

    private Transaction createTxForEthValue(String address, Client client, EtherscanApi.EthTx tx, LocalDateTime ldt, String hash) {
        var amount = parseWei(tx.value());
        var side = tx.from().equals(address) ? Transaction.Type.SELL : Transaction.Type.BUY;
        var ethToken = getEthToken();

        BigDecimal price = etherscanApi.getEtherPriceAt(tx.blockNumber());
        //create eth tx

        return Transaction.builder()
                .platform("Ethereum")
                .id(idGenerator.generate())
                .client(client)
                .amount(amount)
                .type(side)
                .localDateTime(ldt)
                .price(price)
                .token(ethToken.getId())
                .externalId(hash)
                .address(address)
                .build();

    }

    public List<Transaction> refreshTx(Transaction oldTx, Client client) {
        String address = oldTx.getAddress();
        var txs = getEthTxes(address, oldTx.getLocalDateTime(), oldTx.getLocalDateTime());
        var ethTx = txs.stream().filter(t -> t.hash().equals(oldTx.getExternalId())).findFirst().orElseThrow(() -> new NotFoundException("Cant find Eth Tx"));
        var toSave = importOneTx(address, client, ethTx);

        transactionRepo.saveAll(toSave);
        return toSave;
    }

    private Token getEthToken() {
        return tokenRepo.findByTicker("ETH").orElseThrow(() -> new FunctionalException("Cant find Eth Token"));
    }

    private static boolean hasEthValue(EtherscanApi.EthTx tx) {
        return !"0".equals(tx.value());
    }

    private List<EtherscanApi.EthTx> getEthTxes(String address, LocalDateTime from, LocalDateTime to) {
        long fromTimestamp = from.toEpochSecond(ZoneOffset.UTC);
        long toTimestamp = to.toEpochSecond(ZoneOffset.UTC);

        Integer startBlock = etherscanApi.getBlockNumberByTimestamp(
                fromTimestamp, "before");
        if (toTimestamp > (LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(1).toEpochSecond(ZoneOffset.UTC))) {
            toTimestamp = LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(1).toEpochSecond(ZoneOffset.UTC);
        }
        Integer endBlock = etherscanApi.getBlockNumberByTimestamp(toTimestamp,
                "after");

        if (startBlock == null || endBlock == null) {
            throw new FunctionalException(
                    "Invalid block range, failed to get block number from Etherscan API");
        }

        return etherscanApi.getTransactions(
                address,
                startBlock,
                endBlock,
                1,
                10000,
                "asc");
    }

    private boolean iswithdrawal(EventLog log) {
        return log.topics().get(0).equals(WITHDRAW_TOPIC);
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
        var nullPrice = txForHash.stream().filter(t -> t.getPrice().equals(BigDecimal.valueOf(-1))).findFirst();
        var nonNullPrice = txForHash.stream().filter(t -> t.getPrice().compareTo(BigDecimal.valueOf(-1)) != 0).findFirst();
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


    static class TxInterruptionNeeded extends RuntimeException {
    }
}