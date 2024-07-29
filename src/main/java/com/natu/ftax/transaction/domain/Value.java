package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Value {

    private final Token token;
    private final BigDecimal amount;

    public Value(Token token, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            throw new FunctionalException("Value can't be negative: {0} {1}", amount, token);
        }
        this.token = token;
        this.amount = amount;
    }
}
