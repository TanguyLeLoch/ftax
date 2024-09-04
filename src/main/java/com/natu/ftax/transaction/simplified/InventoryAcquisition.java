package com.natu.ftax.transaction.simplified;

import com.natu.ftax.common.exception.FunctionalException;

import java.math.BigDecimal;
import java.math.MathContext;
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
        return switch (tx.getType()) {
            case BUY -> buyFifo(tx);
            case SELL -> sellFifo(tx);
        };
    }

    public Pnl average(TransactionSimplified tx) {
        return switch (tx.getType()) {
            case BUY -> buyAverage(tx);
            case SELL -> sellAverage(tx);
        };
    }

    private Pnl sellFifo(TransactionSimplified tx) {
        // SELL
        BigDecimal pnl = ZERO;
        BigDecimal remainingToSell = tx.getAmount();

        while (remainingToSell.compareTo(ZERO) > 0) {
            if (costQuantities.isEmpty()) {
                throw new FunctionalException("Insufficient quantity for this sell transaction");
            }

            CostQuantity firstIn = costQuantities.getFirst();
            BigDecimal quantityToSell = remainingToSell.min(firstIn.getQuantity());

            BigDecimal priceDiff = tx.getPrice().subtract(firstIn.getPrice());
            pnl = pnl.add(priceDiff.multiply(quantityToSell));

            remainingToSell = remainingToSell.subtract(quantityToSell);

            if (quantityToSell.compareTo(firstIn.getQuantity()) < 0) {
                firstIn.reduceQuantity(quantityToSell);
            } else {
                costQuantities.pollFirst();
            }
        }

        return new Pnl(tx, tokenId, pnl);
    }

    private Pnl buyFifo(TransactionSimplified tx) {
        costQuantities.add(new CostQuantity(tx.getPrice(), tx.getAmount()));
        return Pnl.DUMMY_PNL;
    }


    private Pnl sellAverage(TransactionSimplified tx) {
        // For a sell transaction, calculate PnL based on average cost
        if (costQuantities.isEmpty()) {
            throw new IllegalStateException("No buy transactions recorded for this token");
        }

        CostQuantity averageCostQuantity = costQuantities.getFirst();
        BigDecimal averagePrice = averageCostQuantity.getPrice();
        BigDecimal sellPrice = tx.getPrice();

        BigDecimal pnl = sellPrice.subtract(averagePrice)
            .multiply(tx.getAmount());

        // Update the remaining quantity
        BigDecimal remainingQuantity = averageCostQuantity.getQuantity().subtract(tx.getAmount());
        if (remainingQuantity.compareTo(ZERO) < 0) {
            throw new IllegalStateException("Insufficient quantity for this sell transaction");
        }

        if (remainingQuantity.compareTo(ZERO) == 0) {
            costQuantities.clear();
        } else {
            costQuantities.clear();
            costQuantities.add(
                new CostQuantity(averagePrice, remainingQuantity));
        }

        return new Pnl(tx, tokenId, pnl);
    }

    private Pnl buyAverage(TransactionSimplified tx) {
        // For a buy transaction, update the average cost
        BigDecimal totalQuantity = ZERO;
        BigDecimal totalCost = ZERO;

        for (CostQuantity cq : costQuantities) {
            totalQuantity = totalQuantity.add(cq.getQuantity());
            totalCost = totalCost.add(cq.getPrice().multiply(cq.getQuantity()));
        }

        totalQuantity = totalQuantity.add(tx.getAmount());
        totalCost = totalCost.add(tx.getPrice().multiply(tx.getAmount()));

        costQuantities.clear();
        costQuantities.add(new CostQuantity(
            totalCost.divide(totalQuantity, MathContext.DECIMAL64),
            totalQuantity));

        return Pnl.DUMMY_PNL;
    }


}
