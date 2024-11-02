package com.natu.ftax.transaction.calculation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.natu.ftax.token.Token;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    @ManyToOne
    @JoinColumn(name = "token_id")
    @JsonIgnore
    private Token token;

    public String getTokenId() {
        return token != null ? token.getId() : null;
    }

    private BigDecimal value;

    private String errorMessage;

    public Pnl(String txId, Token token, BigDecimal value) {
        this.txId = txId;
        this.token = token;
        this.value = value;
    }

    public Pnl(String txId, Token token, String errorMessage) {
        this.txId = txId;
        this.token = token;
        this.errorMessage = errorMessage;
    }


}
