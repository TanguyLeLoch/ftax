package com.natu.ftax.transaction.domain;

enum TransactionState {
    DRAFT("draft"),
    SUBMITTED("submitted");

    private final String value;

    TransactionState(String value) {
        this.value = value;
    }

    static TransactionState from(String value) {
        return TransactionState.valueOf(value.toUpperCase());
    }

    public String getValue() {
        return value;
    }


}