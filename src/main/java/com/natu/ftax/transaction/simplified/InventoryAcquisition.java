package com.natu.ftax.transaction.simplified;

import java.math.BigDecimal;
import java.util.LinkedList;

import static java.math.BigDecimal.ZERO;

public class InventoryAcquisition {
    private final String tokenId;
    private final LinkedList<CostQuantity> costQuantities;

    public InventoryAcquisition(String tokenId) {
        this.tokenId = tokenId;
        costQuantities = new LinkedList<>();
    }

    public Pnl fifo(TransactionSimplified tx) {
        if (tx.getType() == TransactionSimplified.Type.BUY) {
            var cosQ = new CostQuantity(tx.getDollarValue(), tx.getAmount());
            costQuantities.add(cosQ);
            return new Pnl(tokenId, ZERO, tx);
        }

        // SELL
        System.out.println("sell");
        BigDecimal pnl = ZERO;

        CostQuantity firstIn = costQuantities.getFirst();
        BigDecimal amountLettred = ZERO;

        while (firstIn.getQuantity().compareTo(tx.getAmount()) <= 0) {
            costQuantities.pollFirst();
            var priceDiff = tx.getPrice().subtract(firstIn.getPrice());
            pnl = pnl.add(priceDiff.multiply(firstIn.getQuantity()));
            amountLettred = amountLettred.add(firstIn.getQuantity());
            firstIn = costQuantities.getFirst();
        }
        var priceDiff = tx.getPrice().subtract(firstIn.getPrice());
        var remaining = tx.getAmount().subtract(amountLettred);
        pnl = pnl.add(priceDiff.multiply(remaining));
        firstIn.reduceQuantity(remaining);


        return new Pnl(tokenId, pnl, tx);
    }

    public Pnl average(TransactionSimplified tx) {
        if (tx.getType() == TransactionSimplified.Type.BUY) {
            // For a buy transaction, update the average cost
            BigDecimal totalQuantity = ZERO;
            BigDecimal totalCost = ZERO;

            for (CostQuantity cq : costQuantities) {
                totalQuantity = totalQuantity.add(cq.getQuantity());
                totalCost = totalCost.add(cq.getCost());
            }

            totalQuantity = totalQuantity.add(tx.getAmount());
            totalCost = totalCost.add(tx.getDollarValue());

            costQuantities.clear();
            costQuantities.add(new CostQuantity(totalCost, totalQuantity));

            return new Pnl(tokenId, ZERO, tx);
        } else {
            // For a sell transaction, calculate PnL based on average cost
            if (costQuantities.isEmpty()) {
                throw new IllegalStateException("No buy transactions recorded for this token");
            }

            CostQuantity averageCostQuantity = costQuantities.getFirst();
            BigDecimal averageCost = averageCostQuantity.getPrice();
            BigDecimal sellPrice = tx.getPrice();

            BigDecimal pnl = sellPrice.subtract(averageCost).multiply(tx.getAmount());

            // Update the remaining quantity
            BigDecimal remainingQuantity = averageCostQuantity.getQuantity().subtract(tx.getAmount());
            if (remainingQuantity.compareTo(ZERO) < 0) {
                throw new IllegalStateException("Insufficient quantity for this sell transaction");
            }

            if (remainingQuantity.compareTo(ZERO) == 0) {
                costQuantities.clear();
            } else {
                costQuantities.clear();
                costQuantities.add(new CostQuantity(averageCost, remainingQuantity));
            }

            return new Pnl(tokenId, pnl, tx);
        }
    }


}
