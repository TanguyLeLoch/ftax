package com.natu.ftax.transaction.simplified;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.natu.ftax.IDgenerator.domain.IdGenerator;
import com.natu.ftax.IDgenerator.infrastructure.SnowflakeIDGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Pnl {


    @Transient
    static final Pnl DUMMY_PNL = new Pnl();

    @Id
    private String txId;

    @NotNull
    private String tokenId;
    @NotNull
    private BigDecimal value;



    public Pnl(TransactionSimplified tx, String tokenId, BigDecimal value) {
        this.txId = tx.getId();
        this.tokenId = tokenId;
        this.value = value;
        tx.setPnl(this);
    }


    @Transient
    @JsonIgnore
    public boolean isNotDummy() {
        return this != DUMMY_PNL;
    }


}
