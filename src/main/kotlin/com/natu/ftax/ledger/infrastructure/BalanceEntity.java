package com.natu.ftax.ledger.infrastructure;

import com.natu.ftax.ledger.domain.Balance;
import com.natu.ftax.transaction.domain.Token;
import jakarta.persistence.*;

@Entity
@Table(name = "balances")
public class BalanceEntity {

    @Id
    private String id;
    private double amount;
    private Token token;

    protected BalanceEntity() {
        // No-arg constructor required by JPA
    }

    public BalanceEntity(String id, double amount, Token token) {
        this.id = id;
        this.amount = amount;
        this.token = token;
    }

    public static BalanceEntity fromDomain(Balance balance) {
        return new BalanceEntity(balance.getId(), balance.getAmount(), balance.getToken());
    }

    public Balance toDomain() {
        return new Balance(id, amount, token);
    }
}
