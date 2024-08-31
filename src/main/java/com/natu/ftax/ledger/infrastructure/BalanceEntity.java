package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.Balance;
import com.natu.ftax.transaction.domain.OldToken;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Table(name = "balances")
public class BalanceEntity {

    @Id
    private String id;
    private BigDecimal amount;
    @Getter
    private String token;

    protected BalanceEntity() {
        // No-arg constructor required by JPA
    }

    public BalanceEntity(String id, BigDecimal amount, String token) {
        this.id = id;
        this.amount = amount;
        this.token = token;
    }

    public static BalanceEntity fromDomain(Balance balance) {
        return new BalanceEntity(balance.getId(), balance.getAmount(), balance.getToken().getSymbol());
    }

    public Balance toDomain() {
        return new Balance(id, amount, new OldToken(token));
    }

}
