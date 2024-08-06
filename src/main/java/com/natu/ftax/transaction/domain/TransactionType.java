package com.natu.ftax.transaction.domain;

import lombok.Getter;

@Getter
enum TransactionType {
    TRANSFER("transfer"), SWAP("swap"), NONE("none");

    private final String value;

    TransactionType(String value) {
        this.value = value;

    }

    static TransactionType from(String value) {
        return TransactionType.valueOf(value.toUpperCase());
    }
}
