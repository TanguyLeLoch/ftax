package com.natu.ftax.transaction.importer.eth;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static com.natu.ftax.transaction.importer.eth.EtherscanApi.TRANSFER_TOPIC;
import static com.natu.ftax.transaction.importer.eth.EtherscanApi.WITHDRAW_TOPIC;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.client.Client;
import com.natu.ftax.common.exception.FunctionalException;
import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.common.imgstorage.ImgDownloader;
import com.natu.ftax.token.Token;
import com.natu.ftax.token.TokenRepo;
import com.natu.ftax.transaction.Transaction;
import com.natu.ftax.transaction.TransactionRepo;
import com.natu.ftax.transaction.importer.MasterTxService;
import com.natu.ftax.transaction.importer.OnChainImporter;
import com.natu.ftax.transaction.importer.eth.model.EventLog;

@Component("EthereumImporter")
public class EthereumImporter implements OnChainImporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EthereumImporter.class);
    static final String WETH = "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2";

    private final EtherscanApi etherscanApi;
    private final IdGenerator idGenerator;
    private final DextoolApi dextoolApi;
    private final TokenRepo tokenRepo;
    private final TransactionRepo transactionRepo;
    private final MasterTxService masterTxService;
    private final ImgDownloader imgDownloader;

    public EthereumImporter(EtherscanApi etherscanApi,
                            IdGenerator idGenerator,
                            DextoolApi dextoolApi,
                            TokenRepo tokenRepo,
                            TransactionRepo transactionRepo,
                            MasterTxService masterTxService,
                            ImgDownloader imgDownloader
    ) {
        this.etherscanApi = etherscanApi;
        this.idGenerator = idGenerator;
        this.dextoolApi = dextoolApi;
        this.tokenRepo = tokenRepo;
        this.transactionRepo = transactionRepo;
        this.masterTxService = masterTxService;
        this.imgDownloader = imgDownloader;
    }

    @Override
    public void importTransaction(String address, LocalDate from,
        LocalDate to, Client client) {

        List<String> existingHashs = transactionRepo.findAllByClient(client).stream()
                .map(Transaction::getExternalId)
                .toList();

        List<EtherscanApi.EthTx> ethTxes = getEthTxes(address, from, to).stream()
                .filter(tx -> !existingHashs.contains(tx.hash()))
                .toList();

        int size = ethTxes.size();
        int cpt = 1;
        for (EtherscanApi.EthTx tx : ethTxes) {
            LOGGER.info("Importing transaction {} ({}/{})", tx.hash(), cpt++, size);
            process1tx(address, client, tx);
        }
    }

    private void process1tx(String address, Client client, EtherscanApi.EthTx tx) {
        List<Transaction> txToSave = new ArrayList<>();
        try {
            txToSave.addAll(importOneTx(address, client, tx));
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
            txToSave.add(emptyTx);
        }
        transactionRepo.saveAll(txToSave);
    }

    private List<Transaction> importOneTx(String address, Client client, EtherscanApi.EthTx tx) {
        List<Transaction> txs = new ArrayList<>();
        var hash = tx.hash();
        var feeTx = processFee(address, client, tx, hash);
        if (feeTx != null) {
            txs.add(feeTx);
        }

        if (!etherscanApi.isStatusOk(tx)) {
            return txs;
        }

        var ldt = convertToLocalDateTime(tx.timeStamp());


        if (hasEthValue(tx)) {
            txs.add(createTxForEthValue(address, client, tx, ldt, hash));
        }

        List<EtherscanApi.InternalTx> internalTxs = etherscanApi.getInternalTxs(tx, address);
        if (!internalTxs.isEmpty()) {
            txs.addAll(createEthFromInternalTx(internalTxs, address, client, tx));
        }

        List<EventLog> logs = etherscanApi.getLogsforTx(tx, address);
        for (var log : logs) {

            if (isWithdrawal(log)) {
                txs.add(importTxFromWithdrawal(address, client, log, ldt, hash, tx));
            } else if (isTransfer(log)) {
                txs.add(importTxFromTransfer(address, client, log, ldt, hash));
            }
        }

        //        var matchedTxs = masterTxService.createMasterTransactions(txs, "Ethereum");

        matchPrices(txs, hash);
        return txs;

    }

    private Transaction processFee(String address, Client client, EtherscanApi.EthTx tx, String hash) {
        if (!address.equalsIgnoreCase(tx.from())) {
            return null;
        }
        BigDecimal fee = getFee(tx);
        return Transaction.builder()
                .platform("Ethereum")
                .id(idGenerator.generate())
                .client(client)

                .localDateTime(convertToLocalDateTime(tx.timeStamp()))
                   .type(Transaction.Type.FEE)

                .amount(fee)
                .token(getEthToken())
                .price(BigDecimal.ZERO)

                .externalId(hash)
                .address(address)

                .build();
    }

    private BigDecimal getFee(EtherscanApi.EthTx tx) {
        var gasUsed = parseWei(tx.gasUsed());
        return gasUsed.multiply(new BigDecimal(tx.gasPrice()));
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
                    .token(getEthToken())
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
                .token(getEthToken())
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
                .token(token)
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

        String logoPath = null;
        if (!isEmpty(info.logo())) {
            String url = "https://www.dextools.io/resources/tokens/logos/" + info.logo().split("\\?")[0];
            logoPath = "logo/ether/" + contractAddress + ".png";
            imgDownloader.downloadAndSaveImage(url, logoPath);
        }
        String wethPair = extractWethPair(info);

        Token token =
            new Token(tokenId, info.symbol(), info.name(), contractAddress, info.decimals(), logoPath, wethPair);
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
                .token(ethToken)
                .externalId(hash)
                .address(address)
                .build();

    }

    public List<Transaction> refreshTx(Transaction oldTx, Client client) {
        String address = oldTx.getAddress();
        var txs = getEthTxes(address, oldTx.getLocalDateTime().toLocalDate(), oldTx.getLocalDateTime().toLocalDate());
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

    private List<EtherscanApi.EthTx> getEthTxes(String address, LocalDate from, LocalDate to) {

        Integer startBlock = getStartBlock(from);
        long toTimestamp = getEndBlock(to);
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

    private static long getEndBlock(LocalDate to) {
        if (to == null || to.atStartOfDay().minusNanos(1)
                              .isAfter(LocalDate.now(ZoneOffset.UTC).atStartOfDay())) {
            return LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1).toEpochSecond(ZoneOffset.UTC);
        }
        return to.atTime(23, 59, 59).toEpochSecond(ZoneOffset.UTC);
    }

    private Integer getStartBlock(LocalDate from) {
        if (from == null || from.isBefore(LocalDate.of(2015, Month.JULY, 30))) {
            return 1;
        }
        long fromTimestamp = from.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        return etherscanApi.getBlockNumberByTimestamp(
            fromTimestamp, "before");
    }

    private boolean isWithdrawal(EventLog log) {
        return log.topics().get(0).equals(WITHDRAW_TOPIC);
    }

    private void matchPrices(List<Transaction> txToSave, String hash) {
        List<Transaction> txForHash =
            txToSave.stream()
                .filter(t -> t.getExternalId().equals(hash))
                .filter(t -> !Transaction.Type.FEE.equals(t.getType())).toList();

        Map<Token, List<Transaction>> groupedByTokenTx = txForHash.stream()
                                                             .collect(Collectors.groupingBy(Transaction::getToken));

        averagePrices(groupedByTokenTx);

        Set<Token> tokensWithPrice = groupedByTokenTx.entrySet().stream()
                                         .filter(entry -> entry.getValue().stream().allMatch(Transaction::hasPrice))
                                         .map(Map.Entry::getKey)
                                         .collect(Collectors.toSet());

        Set<Token> tokensWithoutPrice = groupedByTokenTx.entrySet().stream()
                                            .filter(entry -> entry.getValue().stream().anyMatch(tx -> !tx.hasPrice()))
                                            .map(Map.Entry::getKey)
                                            .collect(Collectors.toSet());

        if (tokensWithoutPrice.size() > 1 || tokensWithoutPrice.size() == 1 && tokensWithPrice.isEmpty()) {
            for (Token token : tokensWithoutPrice) {
                long timestamp = groupedByTokenTx.get(token).get(0).getLocalDateTime().toEpochSecond(ZoneOffset.UTC);
                Integer blockNumber = etherscanApi.getBlockNumberByTimestamp(timestamp, "before");
                BigDecimal price = getPrice(token, blockNumber.toString());
                if (price != null) {
                    groupedByTokenTx.get(token).forEach(t -> t.setPrice(price));
                }
            }
        }
        tokensWithPrice = groupedByTokenTx.entrySet().stream()
                              .filter(entry -> entry.getValue().stream().allMatch(Transaction::hasPrice))
                              .map(Map.Entry::getKey)
                              .collect(Collectors.toSet());

        tokensWithoutPrice = groupedByTokenTx.entrySet().stream()
                                 .filter(entry -> entry.getValue().stream().anyMatch(tx -> !tx.hasPrice()))
                                 .map(Map.Entry::getKey)
                                 .collect(Collectors.toSet());

        if (tokensWithoutPrice.size() != 1 || tokensWithPrice.isEmpty()) {
            return;
        }

        Token missingToken = tokensWithoutPrice.stream().findFirst().get();
        BigDecimal totalAmount = groupedByTokenTx.get(missingToken).stream().reduce(
            BigDecimal.ZERO,
            (sum, tx) -> sum.add(tx.getAmount().multiply(tx.getType().getSign())),
            BigDecimal::add);

        BigDecimal totalValue = BigDecimal.ZERO;
        for (var t : tokensWithPrice) {
            var txs = groupedByTokenTx.get(t);
            for (var tx : txs) {
                totalValue = totalValue.add(tx.getAmount().multiply(tx.getPrice())).multiply(tx.getType().getSign());
            }
        }
        Transaction.Type type = totalValue.compareTo(BigDecimal.ZERO) > 0
                                    ? Transaction.Type.BUY
                                    : Transaction.Type.SELL;

        BigDecimal price = totalValue.abs().divide(totalAmount.abs(), MathContext.DECIMAL64);

        groupedByTokenTx.get(missingToken).forEach(tx -> tx.setPrice(price));
    }

    private BigDecimal getPrice(Token token, String blockNumber) {
        String wethPair = token.getWethPair();
        if (wethPair == null) {
            return null;
        }
        return etherscanApi.getTokenPrice(token, blockNumber, wethPair);
    }

    private void averagePrices(Map<Token, List<Transaction>> groupedByTokenTx) {
        for (List<Transaction> transactions : groupedByTokenTx.values()) {
            // Calculate average price from valid prices in a single pass
            long count = 0;
            BigDecimal sum = BigDecimal.ZERO;
            for (Transaction tx : transactions) {
                if (tx.hasPrice()) {
                    count++;
                    sum = sum.add(tx.getPrice());
                }
            }

            if (count > 0) {
                // Calculate single price for all transactions of this token
                BigDecimal tokenPrice = sum.divide(BigDecimal.valueOf(count), MathContext.DECIMAL64);

                // Set this price for ALL transactions of this token
                transactions.forEach(tx -> tx.setPrice(tokenPrice));
            }
        }
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

    private String extractWethPair(DextoolApi.TokenInfo info) {
        if (info == null || info.reprPair() == null || info.reprPair().id() == null) {
            return null;
        }

        String tokenRef = info.reprPair().id().tokenRef();
        if (tokenRef == null) {
            return null;
        }

        return WETH.equals(tokenRef) ? info.reprPair().id().pair() : null;
    }
    static class TxInterruptionNeeded extends RuntimeException {
    }
}