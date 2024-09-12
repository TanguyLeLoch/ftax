package com.natu.ftax.transaction.calculation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.natu.ftax.transaction.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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


    @Transient
    static final Pnl DUMMY_PNL = new Pnl();

    @Id
    @NotNull
    private String txId;

    @NotNull
    private String tokenId;

    private BigDecimal value;

    private String errorMessage;


    public Pnl(Transaction tx, String tokenId, BigDecimal value) {
        this.txId = tx.getId();
        this.tokenId = tokenId;
        this.value = value;

        tx.setPnl(this);
    }

    public Pnl(Transaction tx, String tokenId, BigDecimal value, String errorMessage) {
        this(tx, tokenId, value);
        this.errorMessage = errorMessage;
    }


    @Transient
    @JsonIgnore
    public boolean isNotDummy() {
        return this != DUMMY_PNL;
    }


}
