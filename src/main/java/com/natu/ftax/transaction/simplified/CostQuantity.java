package com.natu.ftax.transaction.simplified;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CostQuantity {
    private BigDecimal quantity;
    private final BigDecimal price;

    CostQuantity(BigDecimal price, BigDecimal quantity) {
        this.quantity = quantity;
        this.price = price;
    }


    public void reduceQuantity(BigDecimal remaining) {
        quantity = quantity.subtract(remaining);
    }

    @Override
    public String toString() {
        return "CostQuantity{" +
                " quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
