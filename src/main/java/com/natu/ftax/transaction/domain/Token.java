package com.natu.ftax.transaction.domain;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Token {

    @Getter
    private final String symbol;

    public Token(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    @Override
    public String toString() {
        return symbol;
    }
}
