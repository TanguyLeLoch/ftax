package com.natu.ftax.transaction.domain;

import java.util.Locale;

enum TransactionType {
    TRANSFER, SWAP, NONE;

    static TransactionType forValue(String value) {
        return TransactionType.valueOf(value.toUpperCase(Locale.getDefault()));
    }
}
