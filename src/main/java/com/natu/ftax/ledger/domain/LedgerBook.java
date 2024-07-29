package com.natu.ftax.ledger.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LedgerBook {
    @Getter
    private final String id;
    private final List<LedgerEntry> ledgerEntries;

    private LedgerBook(String id) {
        this.id = id;
        this.ledgerEntries = new ArrayList<>();
    }

    public LedgerBook(String id, List<LedgerEntry> ledgerEntries) {
        this.id = id;
        this.ledgerEntries = ledgerEntries;
    }

    public static LedgerBook create(String id) {
        return new LedgerBook(id);
    }

    public List<LedgerEntry> getLedgerEntries() {
        return Collections.unmodifiableList(ledgerEntries);
    }
}
