package com.natu.ftax.transaction.calculation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Pnl {


    @Id
    @NotNull
    private String txId;

    @NotNull
    private String tokenId;

    private BigDecimal value;

    private String errorMessage;

    public Pnl(String txId, String tokenId, BigDecimal value) {
        this.txId = txId;
        this.tokenId = tokenId;
        this.value = value;
    }

    public Pnl(String txId, String tokenId, String errorMessage) {
        this.txId = txId;
        this.tokenId = tokenId;
        this.errorMessage = errorMessage;
    }


}
