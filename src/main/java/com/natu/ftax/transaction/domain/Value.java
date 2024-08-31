package com.natu.ftax.transaction.domain;

import com.natu.ftax.common.exception.FunctionalException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Value {

    private final OldToken oldToken;
    private final BigDecimal amount;


    public Value(OldToken oldToken, BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            throw new FunctionalException("Value cannot be negative: {0} {1}",
                    amount, oldToken);
        }
        this.oldToken = oldToken;
        this.amount = amount;
    }

    public String getSymbol() {
        return oldToken.getSymbol();
    }

    public static Value dummyValue() {
        return new Value(new OldToken("DUM"), BigDecimal.ZERO);
    }
}
