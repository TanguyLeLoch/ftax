package com.natu.ftax.transaction.domain;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class OldToken {

    @Getter
    private final String symbol;

    public OldToken(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    @Override
    public String toString() {
        return symbol;
    }
}
