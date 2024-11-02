package com.natu.ftax.transaction.calculation;

import com.natu.ftax.token.Token;
import com.natu.ftax.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Compute {

    private final List<Transaction> txs;
    private final Map<Token, InventoryAcquisition> inventoryAcquisitions;

    public Compute(List<Transaction> txs) {
        this.txs = txs.stream().sorted().toList();
        this.inventoryAcquisitions = txs.stream()
                .map(Transaction::getToken)
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        InventoryAcquisition::new));

    }

    public List<Transaction> execute(String method) {
        for (var tx : txs) {
            InventoryAcquisition invAcqui = inventoryAcquisitions.get(tx.getToken());
            if (invAcqui.isStopped()) {
                continue;
            }

            if (method.equals("fifo")) {
                invAcqui.fifo(tx);
            } else if (method.equals("average")) {
                invAcqui.average(tx);
            } else throw new RuntimeException("not supported method");
        }

        return txs.stream().filter(tx -> tx.getPnl() != null).toList();

    }
}
