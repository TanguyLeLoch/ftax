package com.natu.ftax.transaction.domain;

enum TransactionState {
    DRAFT("draft"),
    SUBMITTED("submitted");

    private final String value;

    TransactionState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}