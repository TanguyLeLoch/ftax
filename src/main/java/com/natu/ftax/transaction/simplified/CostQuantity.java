package com.natu.ftax.transaction.simplified;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.MathContext;

@Getter
public class CostQuantity {
    private final BigDecimal cost;
    private BigDecimal quantity;
    private final BigDecimal price;

    CostQuantity(BigDecimal cost, BigDecimal quantity) {
        this.cost = cost;
        this.quantity = quantity;
        this.price = cost.divide(quantity, MathContext.DECIMAL64);
    }


    public void reduceQuantity(BigDecimal remaining) {
        quantity = quantity.subtract(remaining);
    }

    @Override
    public String toString() {
        return "CostQuantity{" +
                "cost=" + cost +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
