package com.natu.ftax.transaction.simplified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    static final Pnl DUMMY_PNL = new Pnl(new TransactionSimplified(), null, null);

    @Id
    private String txId;

    @NotNull
    private String tokenId;
    @NotNull
    private BigDecimal value;

    @OneToOne
    @JoinColumn(name = "txId", referencedColumnName = "id")
    private TransactionSimplified transactionSimplified;


    public Pnl(TransactionSimplified tx, String tokenId, BigDecimal value) {

        this.txId = tx.getId();
        this.tokenId = tokenId;
        this.value = value;
        this.transactionSimplified = tx;
        tx.setPnl(this);

    }

    @Transient
    @JsonIgnore
    public boolean isNotDummy() {
        return this != DUMMY_PNL;
    }


}
