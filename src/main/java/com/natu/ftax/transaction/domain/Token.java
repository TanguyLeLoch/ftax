package com.natu.ftax.transaction.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Token {

    @Getter
    private final String symbol;

    public Token(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
