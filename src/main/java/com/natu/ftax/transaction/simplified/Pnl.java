package com.natu.ftax.transaction.simplified;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
public class Pnl {
    @NotNull
    @Getter
    private String tokenId;
    @NotNull
    @Getter
    private BigDecimal value;

    private TransactionSimplified transaction;

    
    public String getTransactionId() {
        return transaction.getId();
    }
}
