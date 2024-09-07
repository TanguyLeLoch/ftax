package com.natu.ftax.transaction.simplified;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Compute {

    private final List<TransactionSimplified> txs;
    private final Map<String, InventoryAcquisition> inventoryAcquisitions;

    Compute(List<TransactionSimplified> txs) {
        this.txs = txs;
        this.inventoryAcquisitions = txs.stream()
                .map(TransactionSimplified::getToken)
                .distinct()
                .collect(Collectors.toMap(
                        token -> token,
                        InventoryAcquisition::new
                ));

    }

    List<TransactionSimplified> execute(String method) {
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
