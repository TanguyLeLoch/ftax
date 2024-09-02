package com.natu.ftax.transaction.simplified;

import java.util.ArrayList;
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

    List<Pnl> execute(String method) {
        var pnls = new ArrayList<Pnl>();
        for (var tx : txs) {
            var invAcqui = inventoryAcquisitions.get(tx.getToken());

            Pnl pnl;
            if (method.equals("fifo")) {
                pnl = invAcqui.fifo(tx);
            } else if (method.equals("average")) {
                pnl = invAcqui.average(tx);
            } else throw new RuntimeException("not supported method");
            pnls.add(pnl);
        }

        return pnls;
    }
}
