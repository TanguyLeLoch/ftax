package com.natu.ftax.transaction.calculation;

import static java.math.BigDecimal.ZERO;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;
import com.natu.ftax.token.Token;
import com.natu.ftax.transaction.Transaction;
import lombok.Getter;

public class InventoryAcquisition {
    private final Token token;
    private final LinkedList<CostQuantity> costQuantities;

    @Getter
    private boolean stopped = false;

    public InventoryAcquisition(Token token) {
        this.token = token;
        costQuantities = new LinkedList<>();
    }

    public void fifo(Transaction tx) {
        switch (tx.getType()) {
            case BUY -> buyFifo(tx);
            case SELL, FEE -> sellFifo(tx);
        }
    }

    public void average(Transaction tx) {
        switch (tx.getType()) {
            case BUY -> buyAverage(tx);
            case SELL, FEE -> sellAverage(tx);
        }
        ;
    }

    private void sellFifo(Transaction tx) {
        // SELL
        BigDecimal pnl = ZERO;
        BigDecimal remainingToSell = tx.getAmount();

        while (remainingToSell.compareTo(ZERO) > 0) {
            if (costQuantities.isEmpty()) {
                this.stopped = true;
                tx.attachErrorPnl(token, "Your balance is insufficient");
                return;
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
        tx.attachErrorPnl(token, pnl);
    }

    private void buyFifo(Transaction tx) {
        costQuantities.add(new CostQuantity(tx.getPrice(), tx.getAmount()));
    }


    private void sellAverage(Transaction tx) {
        // For a sell transaction, calculate PnL based on average cost
        if (costQuantities.isEmpty()) {
            this.stopped = true;
            tx.attachErrorPnl(token, "Your balance is empty");
            return;
        }

        CostQuantity averageCostQuantity = costQuantities.getFirst();
        BigDecimal averagePrice = averageCostQuantity.getPrice();
        BigDecimal sellPrice = tx.getPrice();

        BigDecimal pnl = sellPrice.subtract(averagePrice)
                .multiply(tx.getAmount());

        // Update the remaining quantity
        BigDecimal remainingQuantity = averageCostQuantity.getQuantity().subtract(tx.getAmount());
        if (remainingQuantity.compareTo(ZERO) < 0) {
            this.stopped = true;
            tx.attachErrorPnl(token, "Your balance is insufficient");
        }

        if (remainingQuantity.compareTo(ZERO) == 0) {
            costQuantities.clear();
        } else {
            costQuantities.clear();
            costQuantities.add(
                    new CostQuantity(averagePrice, remainingQuantity));
        }

        tx.attachErrorPnl(token, pnl);
    }

    private void buyAverage(Transaction tx) {
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
    }

}
