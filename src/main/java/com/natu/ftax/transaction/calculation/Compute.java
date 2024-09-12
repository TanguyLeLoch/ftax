package com.natu.ftax.transaction.calculation;

import com.natu.ftax.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Compute {

    private final List<Transaction> txs;
    private final Map<String, InventoryAcquisition> inventoryAcquisitions;

    public Compute(List<Transaction> txs) {
        this.txs = txs;
        this.inventoryAcquisitions = txs.stream()
                .map(Transaction::getToken)
                .distinct()
                .collect(Collectors.toMap(
                        token -> token,
                        InventoryAcquisition::new
                ));

    }

    public List<Transaction> execute(String method) {
        for (var tx : txs) {
            var invAcqui = inventoryAcquisitions.get(tx.getToken());
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
