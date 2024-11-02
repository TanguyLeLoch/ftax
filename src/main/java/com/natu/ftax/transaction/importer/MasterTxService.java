package com.natu.ftax.transaction.importer;

import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.transaction.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MasterTxService {

    private final IdGenerator idGenerator;


    public MasterTxService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    //    public List<Transaction> createMasterTransactions(
    //            List<Transaction> transactions, String platform) {
    //        return transactions.stream()
    //                .collect(Collectors.groupingBy(this::createGroupKey))
    //                .values()
    //                .stream()
    //                .map(grp -> this.createMasterTransaction(grp, platform))
    //                .collect(Collectors.toList());
    //    }

    private String createGroupKey(Transaction tx) {
        return tx.getLocalDateTime().truncatedTo(ChronoUnit.SECONDS)
                + "-" + tx.getToken()
                + "-" + tx.getExternalId();
    }

    private Transaction createMasterTransaction(List<Transaction> group, String platform) {
        if (group.isEmpty()) {
            return null;
        }

        Transaction first = group.get(0);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;


        for (Transaction tx : group) {
            if (tx.getType() == Transaction.Type.BUY) {
                totalAmount = totalAmount.add(tx.getAmount());
            } else {
                totalAmount = totalAmount.subtract(tx.getAmount());
            }
            totalValue = totalValue.add(tx.getAmount().multiply(tx.getPrice()));
        }
        var side = totalAmount.compareTo(BigDecimal.ZERO) > 0 ? Transaction.Type.BUY : Transaction.Type.SELL;
        totalAmount = totalAmount.abs();

        BigDecimal averagePrice = totalValue.divide(totalAmount,
                MathContext.DECIMAL64);
        String id = idGenerator.generate();
        return Transaction.builder()
                .platform(platform)
                .id(id)
                .client(first.getClient())
                .localDateTime(first.getLocalDateTime())
                .type(side)
                .amount(totalAmount)
                .token(first.getToken())
                .price(averagePrice)
                .externalId(first.getExternalId())
                .address(first.getAddress())
                .build();
    }
}
